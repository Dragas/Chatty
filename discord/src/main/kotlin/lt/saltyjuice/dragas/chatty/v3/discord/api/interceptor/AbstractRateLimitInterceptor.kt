package lt.saltyjuice.dragas.chatty.v3.discord.api.interceptor

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import lt.saltyjuice.dragas.chatty.v3.discord.exception.RateLimitException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.*

/**
 * Abstractly implements OkHTTP interceptor to conform to rate limiting standards possibly imposed by internet APIs.
 */
abstract class AbstractRateLimitInterceptor : Interceptor
{
    /**
     * Denotes if this ratelimiter should wait for limit to elapse. Otherwise a [RateLimitException] is thrown
     */
    protected open val shouldWaitForLimit = true

    @Synchronized
    final override fun intercept(chain: Interceptor.Chain): Response
    {
        val request = chain.request()
        if (!canLimit(request))
            return chain.proceed(request)
        val limit = identify(request)
        if (limit != null && isLimited(request, limit))
        {
            if (shouldWaitForLimit)
                waitForLimit(limit)
        }
        val response = chain.proceed(request)
        storeLimit(response)
        return response
    }

    /**
     * @return true, when this limiter can limit particular request
     */
    abstract fun canLimit(request: Request): Boolean

    /**
     * @return true, when this request is supposed to be limited
     */
    open fun isLimited(request: Request, limit: Limit): Boolean
    {
        return getTimeSinceLastRequest(limit) <= getResetDelay() && getCurrentRequestCount(limit) <= getMaxRequests()
    }

    /**
     * Implementations should handle how waiting is handled,
     * before current request can proceed.
     */
    abstract fun waitForLimit(limit: Limit)

    /**
     * Since there can be many different ways how limits are stored and checked for,
     * It's up to implementation to handle how they're saved.
     */
    abstract fun storeLimit(response: Response)

    /**
     * @return Limit corresponding to request object or null, if there isn't one.
     */
    abstract fun identify(request: Request): Limit?

    /**
     * Returns reset delay for particular interceptor in miliseconds.
     */
    abstract fun getResetDelay(): Long

    /**
     * Returns max request count for particular interceptor before it's supposed to be throttled.
     */
    abstract fun getMaxRequests(): Int

    /**
     * Should return the difference in time between last request and now in miliseconds.
     */
    abstract fun getTimeSinceLastRequest(limit: Limit): Long

    /**
     * Should return how many requests have happened since last time limit was reset.
     */
    abstract fun getCurrentRequestCount(limit: Limit): Int


    open class Limit @JvmOverloads constructor(response: Response? = null)
    {
        open val global: Boolean? = response?.header(X_RATELIMIT_GLOBAL)?.toBoolean()
        open val limit: Int? = response?.header(X_RATELIMIT_LIMIT)?.toInt()
        open val remaining: Int? = response?.header(X_RATELIMIT_REMAINING)?.toInt()
        open var reset: Long? = response?.header(X_RATELIMIT_RESET)?.toLong()
        open val isExpired: Boolean
            get()
            {
                return getDelay() <= 0
            }

        open fun getDelay(): Long
        {
            val reset = this.reset ?: throw NullPointerException("Reset epoch was not specified")
            val time = Date().time.div(1000).minus(reset)
            return time
        }

        open fun delayUntilReset() = runBlocking<Unit>()
        {
            if (isExpired)
                return@runBlocking
            val time = getDelay()
            delay(time)
        }

        companion object
        {
            @JvmStatic
            protected val X_RATELIMIT_GLOBAL = "X-RateLimit-Global"
            @JvmStatic
            protected val X_RATELIMIT_LIMIT = "X-RateLimit-Limit"
            @JvmStatic
            protected val X_RATELIMIT_REMAINING = "X-RateLimit-REMAINING"
            @JvmStatic
            protected val X_RATELIMIT_RESET = "X-RateLimit-RESET"
        }
    }


    companion object
    {
        @JvmStatic
        public val PER_GUILD = "guilds"

        @JvmStatic
        public val PER_CHANNEL = "channels"

        @JvmStatic
        public val PER_ACCOUNT = "account"
    }
}
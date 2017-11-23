package lt.saltyjuice.dragas.chatty.v3.discord.api.interceptor

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import lt.saltyjuice.dragas.chatty.v3.discord.exception.RateLimitException
import okhttp3.Interceptor
import okhttp3.Response
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Permits Rate limiting in discord based applications
 *
 * Deprecation notice: You should implement an [AbstractRateLimitInterceptor] or use any of its implementations instead.
 * This will be removed before proper release and currently serves as a notice.
 *
 * @param shouldWait indicates whether or not the interceptor should throw an exception when rate limit is hit
 */
@Deprecated("Implement AbstractRateLimit instead")
open class RateLimitInterceptor @JvmOverloads constructor(protected open val shouldWait: Boolean = false) : Interceptor
{
    @Throws(RateLimitException::class)
    override fun intercept(chain: Interceptor.Chain): Response
    {
        val request = chain.request()
        val requestUrl = request.url().encodedPathSegments()
        val type = requestUrl[2]
        val identifier = requestUrl[3]
        val map = when (type)
        {
            GUILD ->
            {
                limitsPerGuild
            }
            CHANNELS ->
            {
                limitsPerChannel
            }
            else ->
            {
                globalLimits
            }
        }
        var limit = map[identifier]
        if (limit == null)
        {
            limit = Channel(10)
            map[identifier] = limit
        }
        else
        {
            val actualLimit = runBlocking<Limit> { limit!!.receive() }
            if (actualLimit.remaining == 0)
                waitForLimit(actualLimit)
        }

        val response = chain.proceed(request)
        val responseLimit = Limit(response)
        if (responseLimit.reset == null)
        {
            responseLimit.reset = 0
        }
        launch(CommonPool) { limit!!.send(responseLimit) }
        if (response.code() == 429)
        {
            return intercept(chain)
        }
        return response
    }

    /**
     * Waits for limit to pass, if the interceptor is meant to wait. Otherwise, throws [RateLimitException]
     * @throws RateLimitException when limit hasn't expired.
     */
    @Throws(RateLimitException::class)
    protected open fun waitForLimit(limit: Limit)
    {
        if (limit.isExpired)
            return
        if (shouldWait)
            limit.delayUntilReset()
        else
            throw RateLimitException()
    }

    companion object
    {
        @JvmStatic
        protected val CHANNELS = "channels"

        @JvmStatic
        protected val GUILD = "guilds"

        @JvmStatic
        protected val limitsPerGuild: ConcurrentHashMap<String, Channel<Limit>> = ConcurrentHashMap()

        @JvmStatic
        protected val limitsPerChannel: ConcurrentHashMap<String, Channel<Limit>> = ConcurrentHashMap()

        @JvmStatic
        protected val globalLimits: ConcurrentHashMap<String, Channel<Limit>> = ConcurrentHashMap()
    }

    open class Limit(response: Response)
    {
        open val global: Boolean? = response.header(X_RATELIMIT_GLOBAL)?.toBoolean()
        open val limit: Int? = response.header(X_RATELIMIT_LIMIT)?.toInt()
        open val remaining: Int? = response.header(X_RATELIMIT_REMAINING)?.toInt()
        open var reset: Long? = response.header(X_RATELIMIT_RESET)?.toLong()
        open val isExpired: Boolean
            get()
            {
                return getDelay() <= 0
            }

        open fun getDelay(): Long
        {
            val reset = this@Limit.reset ?: throw NullPointerException("Reset epoch was not specified")
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
}
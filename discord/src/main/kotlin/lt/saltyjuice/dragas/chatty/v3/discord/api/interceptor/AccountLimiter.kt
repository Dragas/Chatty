package lt.saltyjuice.dragas.chatty.v3.discord.api.interceptor

import okhttp3.Request
import okhttp3.Response
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Separates account level rate limits from regular rate limits. Any account based rate limits should implement this
 * instead.
 */
abstract class AccountLimiter : AbstractRateLimitInterceptor()
{
    protected val lastRequest = AtomicLong(0)
    protected val requestCount = AtomicInteger(0)

    override fun waitForLimit(limit: Limit)
    {
        val delay = getTimeSinceLastRequest(limit)
        if (delay <= getResetDelay() && delay > 0)
            Thread.sleep(delay)
    }

    override fun storeLimit(response: Response)
    {
        val now = Date().time
        val lastRequestTime = lastRequest.get()
        val delay = now - lastRequestTime
        if (delay > getResetDelay())
        {
            lastRequest.set(now)
            requestCount.set(0)
        }
        requestCount.incrementAndGet()
    }

    override fun getCurrentRequestCount(limit: Limit): Int
    {
        return getCurrentRequestCount()
    }

    open fun getCurrentRequestCount(): Int
    {
        return requestCount.get()
    }

    override fun identify(request: Request): Limit?
    {
        return Limit()
    }

    override fun getTimeSinceLastRequest(limit: Limit): Long
    {
        return getTimeSinceLastRequest()
    }

    open fun getTimeSinceLastRequest(): Long
    {
        val now = Date().time
        val lastRequestTime = lastRequest.get()
        val delay = now - lastRequestTime
        return delay
    }
}
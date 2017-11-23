package lt.saltyjuice.dragas.chatty.v3.discord.api.interceptor

import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.ConcurrentHashMap

open class MessagePostingLimiter : AbstractRateLimitInterceptor()
{
    protected val limits: ConcurrentHashMap<String, Limit> = ConcurrentHashMap()

    override fun canLimit(request: Request): Boolean
    {
        val url = request.url().encodedPathSegments()
        val type = url[2]
        val method = request.method()
        return method == "POST" && type == PER_CHANNEL
    }

    override fun isLimited(request: Request, limit: Limit): Boolean
    {
        val delay = limit.getDelay()
        val remaining = limit.remaining ?: 1
        return remaining < 1 && delay > 0
    }

    override fun waitForLimit(limit: Limit)
    {
        limit.delayUntilReset()
    }

    override fun storeLimit(response: Response)
    {
        val identifier = response.request().url().encodedPathSegments()[3]
        val limit = Limit(response)
        limits[identifier] = limit
    }

    override fun identify(request: Request): Limit?
    {
        val identifier = request.url().encodedPathSegments()[3]
        return limits[identifier]
    }

    override fun getResetDelay(): Long
    {
        return RESET_DELAY
    }

    override fun getMaxRequests(): Int
    {
        return MAX_REQUESTS
    }

    override fun getTimeSinceLastRequest(limit: Limit): Long
    {
        return limit.getDelay()
    }

    override fun getCurrentRequestCount(limit: Limit): Int
    {
        return limit.limit?.minus(limit.remaining ?: 0) ?: getMaxRequests()
    }

    companion object
    {
        @JvmStatic
        val RESET_DELAY = 5000L

        @JvmStatic
        val MAX_REQUESTS = 5
    }
}
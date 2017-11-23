package lt.saltyjuice.dragas.chatty.v3.discord.api.interceptor

import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.ConcurrentHashMap

abstract class ChannelLimiter : AbstractRateLimitInterceptor()
{
    protected val limits: ConcurrentHashMap<String, Limit> = ConcurrentHashMap()

    override fun canLimit(request: Request): Boolean
    {

        val url = request.url().encodedPathSegments()
        if (url.size != getSegmentSize())
            return false
        val globalType = url[2]
        val subtypeSegment = getSubtypeSegmentNumber()
        val subType = url[subtypeSegment]
        val method = request.method()
        return getRequiredMethod().contains(method) && globalType == PER_CHANNEL && subType == getRequiredSubtype()
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

    override fun getTimeSinceLastRequest(limit: Limit): Long
    {
        return limit.getDelay()
    }

    override fun getCurrentRequestCount(limit: Limit): Int
    {
        return limit.limit?.minus(limit.remaining ?: 0) ?: getMaxRequests()
    }

    /**
     * Returns all HTTP methods this limiter supports. Each method needs to be in all caps.
     */
    abstract fun getRequiredMethod(): Array<String>

    /**
     * Returns how many segments should URL consist of to be usable by this interceptor
     */
    abstract fun getSegmentSize(): Int

    /**
     * Returns the required subtype for limited message
     */
    abstract fun getRequiredSubtype(): String

    /**
     * Returns the position of subtype segment
     */
    abstract fun getSubtypeSegmentNumber(): Int
}
package lt.saltyjuice.dragas.chatty.v3.discord.api.interceptor

import okhttp3.Request

open class ReactionLimiter : ChannelLimiter()
{
    override fun getRequiredMethod(): Array<String>
    {
        return arrayOf("PUT", "DELETE")
    }

    override fun getSegmentSize(): Int
    {
        return 7
    }

    override fun getMaxRequests(): Int
    {
        return MAX_REQUESTS
    }

    override fun getResetDelay(): Long
    {
        return RESET_DELAY
    }

    override fun canLimit(request: Request): Boolean
    {
        return super.canLimit(request) && request.url().toString().contains("reaction", true)
    }

    override fun getRequiredSubtype(): String
    {
        return "reactions"
    }

    override fun getSubtypeSegmentNumber(): Int
    {
        return 6
    }

    companion object
    {
        @JvmStatic
        val MAX_REQUESTS = 1

        @JvmStatic
        val RESET_DELAY = 250L
    }
}
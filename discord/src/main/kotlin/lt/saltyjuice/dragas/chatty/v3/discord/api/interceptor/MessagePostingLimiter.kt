package lt.saltyjuice.dragas.chatty.v3.discord.api.interceptor

open class MessagePostingLimiter : ChannelLimiter()
{
    override fun getRequiredMethod(): Array<String>
    {
        return arrayOf("POST")
    }

    override fun getResetDelay(): Long
    {
        return RESET_DELAY
    }

    override fun getMaxRequests(): Int
    {
        return MAX_REQUESTS
    }

    override fun getSegmentSize(): Int
    {
        return 4
    }

    companion object
    {
        @JvmStatic
        val RESET_DELAY = 5000L

        @JvmStatic
        val MAX_REQUESTS = 5
    }
}
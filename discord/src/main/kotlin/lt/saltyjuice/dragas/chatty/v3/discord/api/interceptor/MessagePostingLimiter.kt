package lt.saltyjuice.dragas.chatty.v3.discord.api.interceptor

/**
 * Limits message posting
 */
open class MessagePostingLimiter : MessageLimiter()
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

    companion object
    {
        @JvmStatic
        val RESET_DELAY = 5000L

        @JvmStatic
        val MAX_REQUESTS = 5
    }
}
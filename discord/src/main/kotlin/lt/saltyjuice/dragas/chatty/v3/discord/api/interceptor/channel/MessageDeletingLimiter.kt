package lt.saltyjuice.dragas.chatty.v3.discord.api.interceptor.channel

/**
 * Limits message deleting
 */
open class MessageDeletingLimiter : MessageLimiter()
{
    override fun getRequiredMethod(): Array<String>
    {
        return arrayOf("DELETE")
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
        val RESET_DELAY = 1000L

        @JvmStatic
        val MAX_REQUESTS = 5
    }
}
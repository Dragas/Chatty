package lt.saltyjuice.dragas.chatty.v3.discord.api.interceptor.channel

/**
 * Generifies some constants for message limiters
 */
abstract class MessageLimiter : ChannelLimiter()
{
    override fun getSegmentSize(): Int
    {
        return 5
    }

    override fun getRequiredSubtype(): String
    {
        return "messages"
    }

    override fun getSubtypeSegmentNumber(): Int
    {
        return 4
    }
}
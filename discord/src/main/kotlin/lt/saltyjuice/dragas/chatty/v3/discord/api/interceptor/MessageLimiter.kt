package lt.saltyjuice.dragas.chatty.v3.discord.api.interceptor

abstract class MessageLimiter : ChannelLimiter()
{
    override fun getSegmentSize(): Int
    {
        return 4
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
package lt.saltyjuice.dragas.chatty.v3.discord.api.interceptor.guild

open class PatchMemberLimiter : GuildLimiter()
{
    override fun getRequiredMethod(): Array<String>
    {
        return arrayOf("PATCH")
    }

    override fun getSegmentSize(): Int
    {
        return 6
    }

    override fun getRequiredSubtype(): String
    {
        return "members"
    }

    override fun getSubtypeSegmentNumber(): Int
    {
        return 4
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
        val MAX_REQUESTS = 10

        @JvmStatic
        val RESET_DELAY = 10000L
    }
}
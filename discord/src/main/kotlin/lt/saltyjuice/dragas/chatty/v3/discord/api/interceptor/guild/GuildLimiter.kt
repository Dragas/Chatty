package lt.saltyjuice.dragas.chatty.v3.discord.api.interceptor.guild

import lt.saltyjuice.dragas.chatty.v3.discord.api.interceptor.channel.ChannelLimiter

/**
 * Works just like Channel limiter, but for guild supertype.
 */
abstract class GuildLimiter : ChannelLimiter()
{
    override fun getGlobalType(): String
    {
        return PER_GUILD
    }
}
package lt.saltyjuice.dragas.chatty.v3.discord.message.event

import lt.saltyjuice.dragas.chatty.v3.discord.message.general.UnavailableGuild
import lt.saltyjuice.dragas.chatty.v3.discord.message.request.OPRequest

open class EventGuildDelete : OPRequest<UnavailableGuild>()
{
    companion object
    {
        @JvmStatic
        val EVENT_NAME = "GUILD_DELETE"
    }
}
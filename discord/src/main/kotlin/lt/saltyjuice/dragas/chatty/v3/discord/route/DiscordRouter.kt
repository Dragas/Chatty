package lt.saltyjuice.dragas.chatty.v3.discord.route

import kotlinx.coroutines.experimental.channels.SendChannel
import lt.saltyjuice.dragas.chatty.v3.discord.message.request.OPRequest
import lt.saltyjuice.dragas.chatty.v3.discord.message.response.OPResponse
import lt.saltyjuice.dragas.chatty.v3.websocket.route.WebSocketRouter

open class DiscordRouter(channel: SendChannel<OPResponse<*>>) : WebSocketRouter<OPRequest<*>, OPResponse<*>>(channel)
{

    /**
     * Discord API implementations should use [discordBuilder] instead, as it permits specifying what
     */
    override fun builder(): DiscordRoute.Builder<OPRequest<*>, OPResponse<*>>
    {
        return DiscordRoute.Builder()
    }
}
package lt.saltyjuice.dragas.chatty.v3.discord.route

import lt.saltyjuice.dragas.chatty.v3.websocket.route.WebSocketRouter

open class DiscordRouter : WebSocketRouter()
{

    /**
     * Discord API implementations should use [discordBuilder] instead, as it permits specifying what
     */
    override fun builder(): DiscordRoute.Builder
    {
        return DiscordRoute.Builder()
    }
}
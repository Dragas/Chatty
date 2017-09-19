package lt.saltyjuice.dragas.chatty.v3.discord.main

import lt.saltyjuice.dragas.chatty.v3.core.controller.Controller
import lt.saltyjuice.dragas.chatty.v3.core.main.Client
import lt.saltyjuice.dragas.chatty.v3.discord.route.DiscordRouter
import lt.saltyjuice.dragas.chatty.v3.websocket.main.WebSocketClient

/**
 * Isn't any different from [WebSocketClient] besides having discord oriented calls for tyrus and chatty-websocket.
 * @see WebSocketClient
 * @see Client
 */
open class DiscordClient(vararg controllers: Class<out Controller>) : WebSocketClient(*controllers)
{
    override val router: DiscordRouter = DiscordRouter()
}
package lt.saltyjuice.dragas.chatty.v3.websocket.main

import lt.saltyjuice.dragas.chatty.v3.core.controller.Controller
import lt.saltyjuice.dragas.chatty.v3.core.main.Client
import lt.saltyjuice.dragas.chatty.v3.websocket.route.WebSocketRouter


/**
 * WebSocket client.
 *
 * Core client implementation for WebSockets.
 *
 * Main difference here is that websockets are not clients, but instead servers with either one to many (server) or
 * one to one (client) connection, thus that yields several differences:
 *
 * * [connect] is equivalent to calling [ClientManager.connectToServer].
 * * [isConnected] always returns whether or not [connect] was called.
 * * There's a new method: [disconnect]. It should be called when client is supposed to be stopped.
 *
 * Note: [sin] and [sout] fields should be the same endpoint
 * @see Client
 */
abstract class WebSocketClient(vararg controllers: Class<out Controller>) : Client(*controllers)
{

    abstract override val router: WebSocketRouter
}

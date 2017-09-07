package lt.saltyjuice.dragas.chatty.v3.websocket.main

import kotlinx.coroutines.experimental.channels.Channel
import lt.saltyjuice.dragas.chatty.v3.async.main.AsyncClient
import lt.saltyjuice.dragas.chatty.v3.core.main.Client
import lt.saltyjuice.dragas.chatty.v3.websocket.io.WebSocketInput
import lt.saltyjuice.dragas.chatty.v3.websocket.io.WebSocketOutput
import lt.saltyjuice.dragas.chatty.v3.websocket.route.WebSocketRouter
import org.glassfish.tyrus.client.ClientManager
import java.net.URI
import javax.websocket.ClientEndpointConfig

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
abstract class WebSocketClient<InputBlock, Request, Response, OutputBlock> : AsyncClient<InputBlock, Request, Response, OutputBlock>()
{

    override val sin: WebSocketInput<InputBlock, Request> by lazy()
    {
        endpoint
    }
    override val sout: WebSocketOutput<Response, OutputBlock> by lazy()
    {
        endpoint
    }
    /**
     * Client manager provided by tyrus that is used to connect to websocket based servers.
     */
    open val client: ClientManager = ClientManager.createClient()

    /**
     * Ensures that the implementations uses [WebSocketRouter].
     *
     * While constructing router, you should pass [responseChannel] as the parameter
     */
    abstract override val router: WebSocketRouter<Request, Response>

    /**
     * Provides the URI that the client should connect to via WebSocket.
     */
    protected abstract val uri: URI

    /**
     * Default endpoint configuration.
     *
     * Implementations should override this so that they could provide their own encoders, decoders and other things.
     */
    protected open val cec: ClientEndpointConfig = ClientEndpointConfig.Builder.create().build()

    /**
     * Denotes whether or not the connection has been started.
     */
    protected open var isStarted = false

    /**
     * Returns an endpoint instance that's supposed to work with this client.
     */
    abstract val endpoint: WebSocketEndpoint<InputBlock, Request, Response, OutputBlock>

    override val responseChannel: Channel<Response> by lazy()
    {
        sout.getResponseChannel()
    }

    /**
     * Since websockets are technically servers, connect returns whether or not server managed to start successfully.
     */
    override fun connect(): Boolean
    {
        //val cec = ClientEndpointConfig.Builder.create().build()
        client.connectToServer(endpoint, cec, uri)
        isStarted = true
        return isStarted
    }

    /**
     * For servers, it barely matters if they're connected or not. Thus this instead returns whether or not
     * [connect] was called
     */
    override fun isConnected(): Boolean
    {
        return isStarted
    }

    /**
     * Stops the server and calls [onDisconnect]
     */
    open fun disconnect()
    {
        client.shutdown()
        isStarted = false
        onDisconnect()
    }
}

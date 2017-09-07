package lt.saltyjuice.dragas.chatty.v3.websocket.main

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import lt.saltyjuice.dragas.chatty.v3.websocket.adapter.WebSocketAdapter
import lt.saltyjuice.dragas.chatty.v3.websocket.io.WebSocketInput
import lt.saltyjuice.dragas.chatty.v3.websocket.io.WebSocketOutput
import java.io.IOException
import javax.websocket.*


/**
 * Websocket endpoint.
 *
 * This is a programmatically implemented endpoint, which is meant to simplify websocket implementations.
 */
abstract class WebSocketEndpoint<InputBlock, Request, Response, OutputBlock> : Endpoint(), WebSocketInput<InputBlock, Request>, WebSocketOutput<Response, OutputBlock>
{

    /**
     * An adapter for this endpoint instance.
     *
     * Actually tyrus already has an adapter within client itself, but due to an oversight in `chatty-core`
     * this method shouldn't be called and implemented as `throw NotImplementedException()` and instead
     * serves as a reminder of how it should be linked to tyrus.

     * ```kotlin
     *    override val cec: ClientEndpointConfig = ClientEndpointConfig.Builder.create()
     *        .apply()
     *        {
     *            decoders(listOf(DiscordAdapter::class.java))
     *            encoders(listOf(DiscordAdapter::class.java))
     *        }
     *        .build()
     * ```
     */
    override abstract val adapter: WebSocketAdapter<InputBlock, Request, Response, OutputBlock>

    /**
     * Holds reference to this endpoints' session. This only matters if you intend on sending messages outside
     * Chatty/Core lifecycle, for example ping sort of messages.
     */
    protected open var session: Session? = null

    /**
     * Declares base class for all requests that will be incoming through this endpoint implementation.
     */
    protected abstract val baseClass: Class<Request>

    /**
     * Routes requests to [getRequest].
     */
    private val requests: Channel<Request> = Channel(Channel.UNLIMITED)

    /**
     * Routes responses from [writeResponse] to [responseListener]
     */
    private val responses: Channel<Response> = Channel(Channel.UNLIMITED)

    /**
     * Listens for responses that come through [writeResponse]
     */
    private val responseListener = launch(CommonPool)
    {
        responses.consumeEach(this@WebSocketEndpoint::respond)
    }

    /**
     * By default, called by [responseListener] coroutine to send any responses to open session, if available.
     */
    protected open fun respond(response: Response)
    {
        session?.asyncRemote?.sendObject(response)
    }


    override fun getRequest(): Request = runBlocking<Request>
    {
        return@runBlocking requests.receive()
    }

    override fun writeResponse(response: Response)
    {
        launch(CommonPool)
        {
            responses.send(response)
        }
    }

    override fun getResponseChannel(): Channel<Response>
    {
        return responses
    }


    override fun onOpen(session: Session, config: EndpointConfig)
    {
        this.session = session
        session.addMessageHandler(baseClass, this::handleMessage)
    }

    @Throws(IOException::class)
    override fun onClose(session: Session, reason: CloseReason)
    {
        System.err.println("Session id ${session.id} closed its connection. Reason: ${reason.closeCode.code} - ${reason.reasonPhrase}")
        this.session = null
    }

    @OnError
    override fun onError(session: Session, throwable: Throwable)
    {
        System.err.println("Something happened for session id ${session.id}")
        throwable.printStackTrace()
    }

    /**
     * A callback for when a message is received. This just passes it down the pipeline.
     */
    open protected fun handleMessage(request: Request)
    {
        launch(CommonPool)
        {
            requests.send(request)
        }
    }
}
package lt.saltyjuice.dragas.chatty.v3.websocket.controller

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import lt.saltyjuice.dragas.chatty.v3.core.controller.ConnectionController
import lt.saltyjuice.dragas.chatty.v3.core.event.ConnectionInitEvent
import lt.saltyjuice.dragas.chatty.v3.core.main.Client
import lt.saltyjuice.dragas.chatty.v3.core.route.On
import lt.saltyjuice.dragas.chatty.v3.websocket.event.WebSocketResponseEvent
import org.glassfish.tyrus.client.ClientManager
import java.io.IOException
import java.net.URI
import javax.websocket.*

abstract class WebsocketConnectionController : Endpoint(), ConnectionController
{
    open val client: ClientManager = ClientManager.createClient()
    abstract var uri: URI
    protected open var cec: ClientEndpointConfig.Builder = ClientEndpointConfig.Builder.create()

    /**
     * Holds reference to this endpoints' session. This only matters if you intend on sending messages outside
     * Chatty/Core lifecycle, for example ping sort of messages.
     */
    protected open var session: Session? = null

    /**
     * Declares base class for all requests that will be incoming through this endpoint implementation.
     */
    protected abstract val baseClass: Class<*>

    @On(ConnectionInitEvent::class)
    override final fun handleConnectionInit(payload: Int)
    {
        onBeforeConnect(cec)
        onConnectionInit()
    }

    open fun onConnectionInit()
    {
        try
        {
            val session = client.connectToServer(this, cec.build(), uri)
            onConnect(session)
        }
        catch (err: Exception)
        {
            onConnectionFailure(err)
        }
    }

    open fun onBeforeConnect(cec: ClientEndpointConfig.Builder)
    {

    }

    open fun onConnect(session: Session)
    {

    }

    open fun onConnectionFailure(err: Exception)
    {
        err.printStackTrace(System.err)
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

    override fun onError(session: Session, throwable: Throwable)
    {
        System.err.println("Something happened for session id ${session.id}")
        throwable.printStackTrace()
    }

    /**
     * A callback for when a message is received. This just passes it down the pipeline.
     */
    open protected fun handleMessage(request: Any)
    {
        launch(CommonPool)
        {
            Client.queue(getEventWrapper(request))
        }
    }

    open fun respond(response: Any)
    {
        session?.asyncRemote?.sendObject(response)
    }

    @On(WebSocketResponseEvent::class)
    fun handleResponse(payload: Any)
    {
        respond(payload)
    }
}
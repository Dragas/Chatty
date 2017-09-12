package lt.saltyjuice.dragas.chatty.v3.core.controller

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import lt.saltyjuice.dragas.chatty.v3.core.adapter.Adapter
import lt.saltyjuice.dragas.chatty.v3.core.event.ConnectEvent
import lt.saltyjuice.dragas.chatty.v3.core.event.DisconnectEvent
import lt.saltyjuice.dragas.chatty.v3.core.event.InputEvent
import lt.saltyjuice.dragas.chatty.v3.core.event.ResponseEvent
import lt.saltyjuice.dragas.chatty.v3.core.io.Input
import lt.saltyjuice.dragas.chatty.v3.core.io.Output
import lt.saltyjuice.dragas.chatty.v3.core.route.On

/**
 * Basic implementation of controller oriented towards connection handling.
 *
 * Since Chatty does not check for inherited annotations each overrided method should redeclare respective annotations.
 * Such implementation also permits having multiple connections in the same application, provided you can manage
 * handling multiple request/response schemes.
 */
abstract class ConnectionController<InputBlock, Request, Response, OutputBlock> : Controller, Input<InputBlock, Request>, Output<Response, OutputBlock>
{
    override abstract val adapter: Adapter<InputBlock, Request, Response, OutputBlock>
    protected open var inputListener: Job? = null

    @On(ConnectEvent::class)
    open fun onConnect(payload: Int)
    {
        inputListener = launch(CommonPool)
        {
            while (true)
            {
                val inputEvent = InputEvent(getRequest() as Any)
                queue(inputEvent)
            }
        }
    }

    @On(ResponseEvent::class)
    open fun onResponse(payload: Response)
    {
        writeResponse(payload)
    }

    @On(DisconnectEvent::class)
    open fun onDisconnect(payload: Int)
    {
        inputListener?.cancel()
        inputListener = null
    }
}
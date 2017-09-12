package lt.saltyjuice.dragas.chatty.v3.core.mock.controller

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import lt.saltyjuice.dragas.chatty.v3.core.controller.ConnectionController
import lt.saltyjuice.dragas.chatty.v3.core.event.ConnectEvent
import lt.saltyjuice.dragas.chatty.v3.core.event.OPEvent
import lt.saltyjuice.dragas.chatty.v3.core.mock.adapter.OPAdapter
import lt.saltyjuice.dragas.chatty.v3.core.request.OPRequest
import lt.saltyjuice.dragas.chatty.v3.core.route.On

class OPRequestConnectionController : ConnectionController<String, OPRequest<*>, Any, String>()
{
    override val adapter: OPAdapter = OPAdapter()

    val requestChannel = Channel<String>()

    val responseChannel = Channel<String>()

    init
    {
        default = this
    }

    @On(ConnectEvent::class)
    override fun onConnect(payload: Int)
    {
        inputListener = launch(CommonPool)
        {
            while (true)
            {
                val request = getRequest()
                queue(request)
            }
        }
    }

    fun queue(request: OPRequest<*>)
    {
        queue(OPEvent(request))
    }

    override fun getRequest(): OPRequest<*> = runBlocking()
    {
        val request = requestChannel.receive()
        adapter.deserialize(request)
    }

    override fun writeResponse(response: Any) = runBlocking()
    {
        responseChannel.send(adapter.serialize(response))
    }

    companion object
    {
        @JvmStatic
        private lateinit var default: OPRequestConnectionController

        @JvmStatic
        fun getInstance(): OPRequestConnectionController
        {
            return default
        }
    }
}
package lt.saltyjuice.dragas.chatty.v3.core.mock.controller

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import lt.saltyjuice.dragas.chatty.v3.core.controller.ConnectionController
import lt.saltyjuice.dragas.chatty.v3.core.event.ConnectEvent
import lt.saltyjuice.dragas.chatty.v3.core.event.IntegerInputEvent
import lt.saltyjuice.dragas.chatty.v3.core.mock.adapter.MockAdapter
import lt.saltyjuice.dragas.chatty.v3.core.route.On

class MockConnectionController : ConnectionController<String, Int, Float, String>()
{
    init
    {
        default = this
    }


    override val adapter: MockAdapter = MockAdapter()

    val requestChannel = Channel<String>()

    val responseChannel = Channel<String>()


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

    fun queue(request: Int)
    {
        queue(IntegerInputEvent(request))
    }

    override fun getRequest(): Int = runBlocking<Int>()
    {
        val request = requestChannel.receive()
        adapter.deserialize(request)
    }

    override fun writeResponse(response: Float) = runBlocking()
    {
        val serialized = adapter.serialize(response)
        responseChannel.send(serialized)
    }

    companion object
    {
        @JvmStatic
        private lateinit var default: MockConnectionController

        @JvmStatic
        fun getInstance(): MockConnectionController
        {
            return default
        }
    }
}
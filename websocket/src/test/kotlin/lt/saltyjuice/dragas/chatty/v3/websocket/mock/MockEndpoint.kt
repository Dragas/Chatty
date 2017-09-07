package lt.saltyjuice.dragas.chatty.v3.websocket.mock

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.runBlocking
import lt.saltyjuice.dragas.chatty.v3.websocket.adapter.WebSocketAdapter
import lt.saltyjuice.dragas.chatty.v3.websocket.main.WebSocketEndpoint

class MockEndpoint : WebSocketEndpoint<String, Int, Float, String>()
{
    override val adapter: WebSocketAdapter<String, Int, Float, String> = object : WebSocketAdapter<String, Int, Float, String>()
    {
        override fun serialize(any: Float): String
        {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun deserialize(block: String): Int
        {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
    val testChannel = Channel<Float>()
    override val baseClass: Class<Int> = Int::class.java

    fun handleMockMessage(request: Int)
    {
        handleMessage(request)
    }

    override fun respond(response: Float) = runBlocking()
    {
        testChannel.send(response)
    }
}
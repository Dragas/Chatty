package lt.saltyjuice.dragas.chatty.v3.websocket.mock

import kotlinx.coroutines.experimental.channels.ReceiveChannel
import lt.saltyjuice.dragas.chatty.v3.core.route.UsesControllers
import lt.saltyjuice.dragas.chatty.v3.websocket.main.WebSocketClient
import lt.saltyjuice.dragas.chatty.v3.websocket.route.WebSocketRouter
import java.net.URI


@UsesControllers(MockController::class)
class MockClient : WebSocketClient<String, Int, Float, String>()
{
    override val uri: URI
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override val endpoint: MockEndpoint = MockEndpoint()

    override val router: WebSocketRouter<Int, Float> = WebSocketRouter(endpoint.getResponseChannel())

    fun getResponses(): ReceiveChannel<Float>
    {
        return endpoint.getResponseChannel()
    }
}
package lt.saltyjuice.dragas.chatty.v3.core.mock

import lt.saltyjuice.dragas.chatty.v3.core.controller.Controller
import lt.saltyjuice.dragas.chatty.v3.core.main.Client

class MockClient(vararg controllers: Class<out Controller>) : Client(*controllers)
{
    override val router: MockRouter = MockRouter()

    override fun onConnect()
    {

    }

    override fun onDisconnect()
    {

    }

    override fun connect(): Boolean
    {
        return true
    }

    override fun isConnected(): Boolean
    {
        return true
    }
}
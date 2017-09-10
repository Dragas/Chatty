package lt.saltyjuice.dragas.chatty.v3.core.mock

import lt.saltyjuice.dragas.chatty.v3.core.controller.Controller
import lt.saltyjuice.dragas.chatty.v3.core.main.Client
import lt.saltyjuice.dragas.chatty.v3.core.route.Router

class MockClient(vararg controllers: Class<out Controller>) : Client(*controllers)
{
    override val router: Router
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

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
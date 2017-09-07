package lt.saltyjuice.dragas.chatty.v3.websocket.mock

import lt.saltyjuice.dragas.chatty.v3.async.route.AsyncController
import lt.saltyjuice.dragas.chatty.v3.core.route.On
import lt.saltyjuice.dragas.chatty.v3.core.route.When

class MockController : AsyncController<Float>()
{
    @On(Int::class)
    @When("isOdd")
    fun onOdd(request: Int)
    {
        writeResponse(request.rem(2).toFloat())
    }

    @On(Int::class)
    @When("isEven")
    fun onEven(request: Int)
    {
        writeResponse(request.rem(2).toFloat())
    }

    fun isOdd(request: Int): Boolean
    {
        return request % 2 != 0
    }

    fun isEven(request: Int): Boolean
    {
        return request % 2 == 0
    }
}
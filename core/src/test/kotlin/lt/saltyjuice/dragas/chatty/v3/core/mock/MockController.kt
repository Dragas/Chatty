package lt.saltyjuice.dragas.chatty.v3.core.mock

import lt.saltyjuice.dragas.chatty.v3.core.controller.Controller
import lt.saltyjuice.dragas.chatty.v3.core.route.On
import lt.saltyjuice.dragas.chatty.v3.core.route.When

class MockController : Controller
{
    @On(IntegerInputEvent::class)
    @When("isOdd")
    fun onOdd(request: Int)
    {
        queue(request.toFloat().rem(2))
    }

    fun isOdd(request: Int): Boolean
    {
        return request % 2 != 0
    }
}
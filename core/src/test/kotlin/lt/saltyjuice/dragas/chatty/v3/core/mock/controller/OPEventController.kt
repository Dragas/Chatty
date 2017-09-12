package lt.saltyjuice.dragas.chatty.v3.core.mock.controller

import lt.saltyjuice.dragas.chatty.v3.core.controller.Controller
import lt.saltyjuice.dragas.chatty.v3.core.event.OPEvent
import lt.saltyjuice.dragas.chatty.v3.core.event.ResponseEvent
import lt.saltyjuice.dragas.chatty.v3.core.request.FloatRequest
import lt.saltyjuice.dragas.chatty.v3.core.request.IntRequest
import lt.saltyjuice.dragas.chatty.v3.core.route.On
import lt.saltyjuice.dragas.chatty.v3.core.route.When

class OPEventController : Controller
{
    @On(OPEvent::class)
    @When("itIsOdd")
    fun onOdd(request: FloatRequest)
    {
        queue(request.payload!!.rem(2))
    }

    @On(OPEvent::class)
    @When("itIsOdd")
    fun onOdd(request: IntRequest)
    {
        queue(request.payload!!.rem(2))
    }

    fun itIsOdd(request: FloatRequest): Boolean
    {
        return request.payload!!.rem(2) != 0f
    }

    fun itIsOdd(request: IntRequest): Boolean
    {
        return request.payload!!.rem(2) != 0
    }

    fun queue(response: Float)
    {
        queue(ResponseEvent(response))
    }

    fun queue(response: Int)
    {
        queue(ResponseEvent(response))
    }
}
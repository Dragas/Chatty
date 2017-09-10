package lt.saltyjuice.dragas.chatty.v3.core.controller

import lt.saltyjuice.dragas.chatty.v3.core.event.LambdaEvent
import lt.saltyjuice.dragas.chatty.v3.core.route.On

open class LambdaController : Controller
{
    @On(LambdaEvent::class)
    fun onConsume(lambda: () -> Unit)
    {
        try
        {
            lambda()
        }
        catch (err: Throwable)
        {
            err.printStackTrace(System.err)
        }
    }
}
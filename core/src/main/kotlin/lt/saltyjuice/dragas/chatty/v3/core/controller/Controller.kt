package lt.saltyjuice.dragas.chatty.v3.core.controller

import lt.saltyjuice.dragas.chatty.v3.core.event.Event
import lt.saltyjuice.dragas.chatty.v3.core.event.LambdaEvent
import lt.saltyjuice.dragas.chatty.v3.core.event.ResponseEvent
import lt.saltyjuice.dragas.chatty.v3.core.main.Client
import java.util.*

/**
 * Base class for all controllers.
 *
 * Design wise, all helpers methods for formatting responses should be provided here.
 */
interface Controller
{
    /**
     * Send the event back to event queue
     */
    fun queue(response: Event)
    {
        Client.queue(response)
    }


    fun queue(lambda: () -> Unit)
    {
        queue(lambda, 0)
    }

    fun queue(lambda: () -> Unit, delay: Long)
    {
        val date = Date()
        date.time += delay
        queue(LambdaEvent(lambda, date))
    }

    fun queue(response: Any)
    {
        queue(ResponseEvent(response))
    }
}
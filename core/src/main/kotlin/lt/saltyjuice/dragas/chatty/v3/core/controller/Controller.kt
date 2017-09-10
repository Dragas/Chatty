package lt.saltyjuice.dragas.chatty.v3.core.controller

import lt.saltyjuice.dragas.chatty.v3.core.event.Event
import lt.saltyjuice.dragas.chatty.v3.core.event.LambdaEvent
import lt.saltyjuice.dragas.chatty.v3.core.main.Client

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
        Client.queue(LambdaEvent(lambda))
    }
}
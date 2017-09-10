package lt.saltyjuice.dragas.chatty.v3.core.route

import lt.saltyjuice.dragas.chatty.v3.core.Event
import lt.saltyjuice.dragas.chatty.v3.core.main.Client

/**
 * Base class for all controllers.
 *
 * Design wise, all helpers methods for formatting responses should be provided here.
 */
open class Controller
{
    /**
     * Send the event back to event queue
     */
    protected open fun queue(response: Event)
    {
        Client.queue(response)
    }
}
package lt.saltyjuice.dragas.chatty.v3.core.controller

import lt.saltyjuice.dragas.chatty.v3.core.event.Event

/**
 * Basic implementation of controller oriented towards connection handling.
 */
interface ConnectionController : Controller
{
    fun handleConnectionInit(payload: Int)

    fun handleDisconnect(payload: Int)

    fun getEventWrapper(request: Any): Event
}
package lt.saltyjuice.dragas.chatty.v3.websocket.event

import lt.saltyjuice.dragas.chatty.v3.core.event.Event

open class WebSocketResponseEvent(override val payload: Any) : Event(payload)
{
}
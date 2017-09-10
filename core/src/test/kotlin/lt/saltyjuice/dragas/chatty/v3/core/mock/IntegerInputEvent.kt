package lt.saltyjuice.dragas.chatty.v3.core.mock

import lt.saltyjuice.dragas.chatty.v3.core.event.InputEvent

class IntegerInputEvent(override val payload: Int) : InputEvent(payload)
{
}
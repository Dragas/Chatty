package lt.saltyjuice.dragas.chatty.v3.core.event

import lt.saltyjuice.dragas.chatty.v3.core.request.OPRequest

class OPEvent(override val payload: OPRequest<*>) : Event(payload)
{

}
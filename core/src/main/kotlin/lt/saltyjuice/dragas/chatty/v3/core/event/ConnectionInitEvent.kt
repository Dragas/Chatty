package lt.saltyjuice.dragas.chatty.v3.core.event

open class ConnectionInitEvent @JvmOverloads constructor(override val payload: Int = 1) : Event(payload)
{
}
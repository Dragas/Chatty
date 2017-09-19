package lt.saltyjuice.dragas.chatty.v3.core.event

open class LambdaEvent constructor(override val payload: () -> Unit) : Event(payload)
{

}
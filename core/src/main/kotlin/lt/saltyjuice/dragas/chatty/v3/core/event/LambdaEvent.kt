package lt.saltyjuice.dragas.chatty.v3.core.event

import java.util.*

open class LambdaEvent @JvmOverloads constructor(override val payload: () -> Unit, date: Date = Date()) : Event(payload, date)
{

}
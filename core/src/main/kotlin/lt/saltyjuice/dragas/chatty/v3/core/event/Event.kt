package lt.saltyjuice.dragas.chatty.v3.core.event

import java.util.*

open class Event @JvmOverloads constructor(open val payload: Any, open val executeAt: Date = Date())
{

}
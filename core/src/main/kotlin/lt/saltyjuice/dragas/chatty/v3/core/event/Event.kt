package lt.saltyjuice.dragas.chatty.v3.core.event

import java.util.*

/**
 * Base class for all event types. Implementations need to override payload type to match
 * the target listener's parameter so that framework could properly handle it.
 */
open class Event @JvmOverloads constructor(open val payload: Any, open val executeAt: Date = Date())
{

}
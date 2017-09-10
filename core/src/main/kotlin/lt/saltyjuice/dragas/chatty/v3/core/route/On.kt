package lt.saltyjuice.dragas.chatty.v3.core.route

import lt.saltyjuice.dragas.chatty.v3.core.event.Event
import kotlin.reflect.KClass

/**
 * Marks that particular method in [Controller] is used as a callback for particular events
 */
@MustBeDocumented
@Retention()
@Target(AnnotationTarget.FUNCTION)
annotation class On(val clazz: KClass<out Event>)
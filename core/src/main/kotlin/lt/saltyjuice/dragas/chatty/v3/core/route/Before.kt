package lt.saltyjuice.dragas.chatty.v3.core.route

import lt.saltyjuice.dragas.chatty.v3.core.middleware.BeforeMiddleware
import kotlin.reflect.KClass

/**
 * Marks that particular callback or controller or client uses mentioned middleware before using a request.
 *
 * All middlewares are added to routes generated by marked context. Thus Client middlewares are added to all routes,
 * controller middlewares are added to all routes from that controller and method middleware is added only to that route.
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention()
annotation class Before(vararg val value: KClass<out BeforeMiddleware<*>>)
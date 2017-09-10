package lt.saltyjuice.dragas.chatty.v3.core.route

import lt.saltyjuice.dragas.chatty.v3.core.Event
import lt.saltyjuice.dragas.chatty.v3.core.exception.RouteBuilderException


/**
 * Router object, which handles all the [Route] objects that are used within the application.
 *
 * @param Request a wrapper object obtained from [Deserializer] implementation
 * @param Response a wrapper object returned from callback of corresponding route
 */
abstract class Router
{
    protected open val routes: HashMap<Class<out Event>, MutableList<Route>> = HashMap()
    /**
     * Returns a route builder, which handles assigning middlewares, callback and test callback and builds routes that can
     * be used by this router.
     */
    abstract fun builder(): Route.Builder

    /**
     * Adds a route to router, which is later used to test requests
     */
    open fun add(route: Route)
    {
        val list = routes[route.type] ?: mutableListOf()
        if (!list.contains(route))
            list.add(route)
        routes[route.type] = list
    }

    /**
     * a shorthand to build and add a route.
     */
    open fun add(route: Route.Builder)
    {
        add(route.build())
    }

    open fun add(routes: List<Route.Builder>)
    {
        routes.forEach(this::add)
    }

    /**
     * Attempts consuming provided [request] request. Returns null on failure.
     *
     * Implementations should take into consideration, that there are global middlewares that should be tested against.
     *
     * Your payload MAY implement [Cloneable] interface, which permits your data being cloned, if it does, you may then
     * modify the request while testing in test callbacks freely, without influencing other routes.
     */
    @Throws(IllegalStateException::class)
    fun consume(event: Event)
    {
        val routes = routes[event.javaClass] ?: return
        routes.parallelStream().forEach()
                {
                    val payload = if (event.payload is Cloneable) event.payload.javaClass.getMethod("clone").invoke(event.payload) else event.payload
                    val eventClone = event.javaClass.getDeclaredConstructor(payload.javaClass).newInstance(payload)
                    it.attemptTrigger(eventClone)
                }
    }

    /**
     * Scraps particular controller for methods that have [On] annotation and then builds routes for them.
     */
    @Throws(RouteBuilderException::class)
    fun consume(controller: Class<out Controller>)
    {
        add(builder().consume(controller))
    }
}
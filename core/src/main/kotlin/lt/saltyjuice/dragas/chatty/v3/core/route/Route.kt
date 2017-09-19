package lt.saltyjuice.dragas.chatty.v3.core.route

import lt.saltyjuice.dragas.chatty.v3.core.controller.Controller
import lt.saltyjuice.dragas.chatty.v3.core.event.Event
import lt.saltyjuice.dragas.chatty.v3.core.exception.RouteBuilderException
import java.lang.reflect.Method
import kotlin.streams.toList

/**
 * A simple route class that holds all abstraction related to Routing in general.
 *
 * Key idea here is that [Request] objects are matched by [testCallback] callback and then a
 * [callback] is triggered, which is supposed to generate a non-nullable [Response] object. Since [callback]
 * is a lambda reference, you can implement MVC pattern (or model-response-controller) around
 * route objects.
 */
open class Route
{
    /**
     * Invoked when this route passes all tests.
     */
    protected open var callback: Method? = null
    /**
     * Holds reference to controller class, which is later used to create new controller instances.
     */
    protected lateinit open var controllerClazz: Class<out Controller>
    /**
     * Provides description for this route.
     */
    protected open var description: String = ""
    /**
     * Holds current controller instance
     */
    protected open var controllerInstanceHolder: Controller? = null

    /**
     * Holds reference to type of this route's trigger type
     */
    open lateinit var type: Class<out Event>

    /**
     * Holds reference to callback's parameter type
     */
    protected open var paramterType: Class<*>? = null

    /**
     * Invoked, when this route is being tested.
     */
    protected open var testCallback: Method? = null

    open fun getControllerInstance(): Controller
    {
        return controllerInstanceHolder!!
    }

    /**
     * Tests the request by first checking whether or not it passes the middleware test,
     * only after it does it test for actual patterns.
     */
    open fun canTrigger(request: Event): Boolean
    {
        return request::class.java.isAssignableFrom(type) &&
                paramterType?.isAssignableFrom(request.payload::class.javaObjectType) ?: true &&
                testCallback?.invoke(getControllerInstance(), request.payload) as? Boolean ?: true
    }

    /**
     * Attempts consuming the provided request.
     */
    open fun attemptTrigger(request: Event)
    {
        if (canTrigger(request))
        {
            callback?.invoke(getControllerInstance(), request.payload)
        }
    }

    /**
     * The base route builder for all Chatty implementations.
     *
     * Contains several very basic methods that help you build routes for [Router]
     */
    abstract class Builder
    {
        protected open var mCallback: Method? = null
        protected open var mTestCallback: Method? = null
        protected open var mDescription: String = ""
        protected open var mType: Class<out Event>? = null
        protected open lateinit var mControllerClazz: Class<out Controller>
        private var superConsumeMethodCalledWhenBuilding = false
        private var superAdaptCalled = false

        open fun callback(callback: Method): Builder
        {
            this.mCallback = callback
            return this
        }

        open fun testCallback(callback: Method): Builder
        {
            this.mTestCallback = callback
            return this
        }

        open fun description(string: String): Builder
        {
            this.mDescription = string
            return this
        }


        open fun controller(clazz: Class<out Controller>): Builder
        {
            this.mControllerClazz = clazz
            return this
        }

        open fun type(clazz: Class<out Event>): Builder
        {
            this.mType = clazz
            return this
        }

        fun build(): Route
        {
            val route = adapt(returnableRoute())
            if (!superAdaptCalled)
                throw RouteBuilderException("super.adapt() was not called!")
            return route
        }

        /**
         * Implementations should return a raw route object which is later used in [adapt] to add all the callbacks, middlewares, etc.
         */
        abstract fun returnableRoute(): Route

        /**
         * Copies all fields from this [Builder] to provided route. Anyone overriding this method MUST call super.adapt(route)
         */
        open fun adapt(route: Route): Route
        {
            route.type = mType!!
            route.description = mDescription
            route.callback = mCallback
            route.testCallback = mTestCallback
            route.controllerClazz = mControllerClazz
            route.controllerInstanceHolder = getController(mControllerClazz)
            route.paramterType = mTestCallback?.parameterTypes?.get(0)?.kotlin?.javaObjectType
            superAdaptCalled = true
            return route
        }

        /**
         * Consumes controller class with provided global middlewares.
         *
         * @return a list of all route builders that may build a route.
         */
        @Throws(RouteBuilderException::class)
        fun consume(controller: Class<out Controller>): List<Route.Builder>
        {
            return controller
                    .methods
                    .toList()
                    .stream()
                    .filter { it.getAnnotation(On::class.java) != null }
                    .peek { if (it.parameterCount != 1) throw RouteBuilderException("Too many parameters for method ${it.declaringClass.canonicalName}#${it.name}") }
                    .map()
                    { method ->
                        val builder = this.javaClass.newInstance()
                        builder.consume(controller, method)
                        if (!builder.superConsumeMethodCalledWhenBuilding)
                            throw RouteBuilderException("super.consume(controller, method) was not called.")
                        builder
                    }
                    .toList()
        }

        /**
         * Should any additional annotations be added later in core or protocol implementations, they should be scrapped here.
         * Calling super is mandatory.
         */
        @Throws(RouteBuilderException::class)
        open fun consume(controller: Class<out Controller>, method: Method): Route.Builder
        {
            val parameter = method.parameterTypes[0].kotlin.javaObjectType
            val onEventAnnotation = method.getAnnotation(On::class.java).clazz.javaObjectType
            val fieldType = onEventAnnotation.getMethod("getPayload").returnType
            if (!fieldType.isAssignableFrom(parameter)) throw RouteBuilderException("Unable to cast ${parameter.canonicalName} to ${fieldType.canonicalName}")
            type(onEventAnnotation)
            description(method.getAnnotation(Description::class.java)?.value ?: "${controller.canonicalName}#${method.name}")
            controller(controller)
            callback(method)
            method.getAnnotation(When::class.java)?.apply()
            {
                val testMethods = controller.methods
                        .filter { it.name == value }
                        .filter { it.parameterCount == 1 }
                        .filter { it.returnType.kotlin.javaObjectType.isAssignableFrom(Boolean::class.javaObjectType) }
                val testMethod = testMethods.find { it.parameterTypes[0].kotlin.javaObjectType.isAssignableFrom(parameter) }
                        ?: throw RouteBuilderException("Unable to find method named $value in ${controller.canonicalName}. Note: It needs to have a single parameter that matches in @On annotation.")
                testCallback(testMethod)
            }
            superConsumeMethodCalledWhenBuilding = true
            return this
        }
    }
}
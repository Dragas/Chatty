package lt.saltyjuice.dragas.chatty.v3.core.main

import lt.saltyjuice.dragas.chatty.v3.core.exception.InitializeAlreadyCalledException
import lt.saltyjuice.dragas.chatty.v3.core.exception.InitializeNotCalledException
import lt.saltyjuice.dragas.chatty.v3.core.exception.RouteBuilderException
import lt.saltyjuice.dragas.chatty.v3.core.io.Input
import lt.saltyjuice.dragas.chatty.v3.core.io.Output
import lt.saltyjuice.dragas.chatty.v3.core.middleware.AfterMiddleware
import lt.saltyjuice.dragas.chatty.v3.core.middleware.BeforeMiddleware
import lt.saltyjuice.dragas.chatty.v3.core.middleware.MiddlewareUtility
import lt.saltyjuice.dragas.chatty.v3.core.route.Controller
import lt.saltyjuice.dragas.chatty.v3.core.route.Route
import lt.saltyjuice.dragas.chatty.v3.core.route.Router
import lt.saltyjuice.dragas.chatty.v3.core.route.UsesControllers
import java.lang.Exception
import java.net.Socket

/**
 * An abstraction which defines how bot's client should be defined. Usually the pipeline
 * will be implemented as follows:
 * * [Client] connects to some server via [Socket]
 * * [Client] binds [Socket]'s input and output streams with [Input] and [Output] wrappers.
 * * [Client] requests some data from [Input], which handles wrapping data to something usable by the
 * bot.
 * * [Client] passes [Request] object to [Router], which handles testing [Route] objects against the provided Request
 * * [Client] passes [Response] object if it's returned by [Router] to [Output] wrapper, which handles
 * deserializing the Response
 * * [Client] then goes back to 3rd step unless the [Socket] is closed.
 *
 * Such behavior can be wrapped in multithreading environment, if necessary, as most of the time adapters
 * and router will not have any variable data in themselves and Input and Output streams have [Synchronized]
 * annotation on them.
 *
 * By default, [Client] does not have an internal socket, as some implementations won't be using it.
 * It's up to the implementing module to decide what will it use as a "Socket"
 */
abstract class Client<InputBlock, Request, Response, OutputBlock>
{
    /**
     * A wrapper for socket's input stream, which is used to deserialize provided data.
     */
    protected abstract val sin: Input<InputBlock, Request>
    /**
     * A wrapper for socket's output stream, which is used to serialize generated data by the bot.
     */
    protected abstract val sout: Output<Response, OutputBlock>
    /**
     * Handles testing of [Request] wrappers.
     */
    protected abstract val router: Router<Request, Response>

    private var initialized = false

    /**
     * Implementations should handle how the client itself is initialized: for example routes,
     * client settings, thread pools, etc.
     *
     * Implementations must call super.initialize()
     */
    @Throws(InitializeAlreadyCalledException::class)
    open fun initialize()
    {
        if (initialized)
            throw InitializeAlreadyCalledException()
        val beforeMiddlewares = MiddlewareUtility.getBeforeMiddlewares(this) as List<Class<out BeforeMiddleware<Request>>>
        val afterMiddlewares = MiddlewareUtility.getAfterMiddlewares(this) as List<Class<out AfterMiddleware<Response>>>
        val annotation = javaClass.getAnnotation(UsesControllers::class.java) ?: throw IllegalStateException("This client doesn't have any callbacks.")
        annotation.value.forEach()
        { controller ->
            try
            {
                router.consume(controller.java as Class<out Controller<Response>>, beforeMiddlewares, afterMiddlewares)
            }
            catch (err: Exception)
            {
                throw RouteBuilderException("Failed to consume ${controller.java.canonicalName}", err)
            }
        }
        initialized = true
    }

    /**
     * Implementations should handle how the client acts once socket has successfully connected
     *
     * For proper behavior, super needs to be called.
     */
    abstract fun onConnect()

    /**
     * Implementations should handle how the client acts once the socket has disconnected. Usually
     * it will just clean after itself: close any loggers it had, etc.
     */
    abstract fun onDisconnect()

    /**
     * runs the pipeline partially
     */
    open fun run()
    {
        val request = sin.getRequest()
        router.consume(request).forEach(this::writeResponse)
    }

    /**
     * Writes [response] to [sout]
     *
     * @param response a response generated by implementing application
     */
    protected fun writeResponse(response: Response)
    {
        sout.writeResponse(response)
    }

    /**
     * Runs the pipeline, which is defined in [Client] comment.
     */
    fun work()
    {
        initialize()
        if (!initialized)
            throw InitializeNotCalledException()
        if (connect())
        {
            onConnect()
            while (isConnected())
                run()
        }
        onDisconnect()
    }

    /**
     * Implementations should handle how the client connects
     * @return true, if connection succeeds
     */
    abstract fun connect(): Boolean

    /**
     * Implementations should determine themselves on whether or not the client is still connected.
     * @return true, if the client is still connected
     */
    abstract fun isConnected(): Boolean
}
package lt.saltyjuice.dragas.chatty.v3.core.main

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.consumeEach
import lt.saltyjuice.dragas.chatty.v3.core.controller.Controller
import lt.saltyjuice.dragas.chatty.v3.core.event.Event
import lt.saltyjuice.dragas.chatty.v3.core.exception.InitializeAlreadyCalledException
import lt.saltyjuice.dragas.chatty.v3.core.exception.InitializeNotCalledException
import lt.saltyjuice.dragas.chatty.v3.core.exception.RouteBuilderException
import lt.saltyjuice.dragas.chatty.v3.core.io.Input
import lt.saltyjuice.dragas.chatty.v3.core.io.Output
import lt.saltyjuice.dragas.chatty.v3.core.route.Route
import lt.saltyjuice.dragas.chatty.v3.core.route.Router
import java.lang.Exception
import java.net.Socket
import java.util.*

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
abstract class Client(vararg protected open val controllers: Class<out Controller>)
{
    protected abstract val router: Router

    private var initialized = false

    protected open var eventQueue: Channel<Event> = Channel(1000)

    protected open var eventQueueListeners: List<Job> = listOf()

    protected open val eventQueueListenerCount = Runtime.getRuntime().availableProcessors()
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
        controllers.forEach()
        { controller ->
            try
            {
                router.consume(controller)
            }
            catch (err: Exception)
            {
                throw RouteBuilderException("Failed to consume ${controller.canonicalName}", err)
            }
        }
        initializeEventQueueListeners()
        mDefault = this
        initialized = true
    }

    /**
     * Initializes event queue listeners
     */
    open fun initializeEventQueueListeners()
    {
        val mutable = mutableListOf<Job>()
        repeat(eventQueueListenerCount)
        {
            mutable.add(launch(CommonPool)
            {
                eventQueue.consumeEach(this@Client::consumeEvent)
            })
        }
        eventQueueListeners = mutable
    }

    /**
     * Pushes event to router if execution time has come
     */
    open fun consumeEvent(event: Event)
    {
        val now = Date()
        if (event.executeAt > now)
        {
            launch(CommonPool)
            {
                delay(event.executeAt.time - now.time)
                eventQueue.send(event)
            }
            return
        }
        router.consume(event)
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
    open fun consume() = runBlocking()
    {
        consumeEvent(eventQueue.receive())
    }

    open fun queueEvent(event: Event)
    {
        launch(CommonPool)
        {
            eventQueue.send(event)
        }
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
                consume()
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

    companion object
    {
        @JvmStatic
        private lateinit var mDefault: Client

        @JvmStatic
        val default: Client
            get()
            {
                return mDefault
            }

        @JvmStatic
        fun queue(event: Event)
        {
            default.queueEvent(event)
        }
    }
}
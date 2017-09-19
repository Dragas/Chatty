package lt.saltyjuice.dragas.chatty.v3.core.main

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import lt.saltyjuice.dragas.chatty.v3.core.controller.Controller
import lt.saltyjuice.dragas.chatty.v3.core.event.ConnectionInitEvent
import lt.saltyjuice.dragas.chatty.v3.core.event.Event
import lt.saltyjuice.dragas.chatty.v3.core.exception.InitializeAlreadyCalledException
import lt.saltyjuice.dragas.chatty.v3.core.exception.InitializeNotCalledException
import lt.saltyjuice.dragas.chatty.v3.core.exception.RouteBuilderException
import lt.saltyjuice.dragas.chatty.v3.core.route.Route
import lt.saltyjuice.dragas.chatty.v3.core.route.Router
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
abstract class Client(vararg protected open val controllers: Class<out Controller>)
{
    protected abstract val router: Router

    private var initialized = false

    protected open var eventQueueListeners: List<Job> = listOf()

    protected open val eventQueueListenerCount = Runtime.getRuntime().availableProcessors()

    protected open var isRunning = false
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
        initialized = true
        queue(ConnectionInitEvent())
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
        router.consume(event)
    }

    /**
     * runs the pipeline partially
     */
    open fun consume() = runBlocking()
    {
        consumeEvent(eventQueue.receive())
    }

    /**
     * Runs the pipeline, which is defined in [Client] comment.
     */
    fun work()
    {
        initialize()
        if (!initialized)
            throw InitializeNotCalledException()
        isRunning = true
        initializeEventQueueListeners()
        while (isRunning)
            consume()
    }

    companion object
    {
        private val eventQueue: Channel<Event> = Channel(1000)

        @JvmStatic
        fun queue(event: Event) = launch(CommonPool)
        {
            eventQueue.send(event)
        }
    }
}
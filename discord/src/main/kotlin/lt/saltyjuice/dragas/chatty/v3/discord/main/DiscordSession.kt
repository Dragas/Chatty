package lt.saltyjuice.dragas.chatty.v3.discord.main

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import lt.saltyjuice.dragas.chatty.v3.core.main.Client
import lt.saltyjuice.dragas.chatty.v3.discord.Settings
import lt.saltyjuice.dragas.chatty.v3.discord.message.event.EventReady
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.Hello
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.Identify
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.Ready
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.Resume
import lt.saltyjuice.dragas.chatty.v3.discord.message.request.*
import lt.saltyjuice.dragas.chatty.v3.discord.message.response.GatewayHeartbeat
import lt.saltyjuice.dragas.chatty.v3.discord.message.response.GatewayIdentify
import lt.saltyjuice.dragas.chatty.v3.discord.message.response.GatewayResume
import java.util.*
import javax.websocket.Session

open class DiscordSession(private val session: Session)
{
    protected open lateinit var job: Job
    protected open lateinit var identifyWrapper: Identify
    protected open var hello: Hello? = null
    protected open var initialized = false
    protected open val maxBeats = 3
    protected open var lastAck: Date? = null
        @Synchronized
        get()
        {
            return field
        }
        @Synchronized
        set(it)
        {
            field = it
        }
    protected var ready: Ready? = null
    protected var sequenceNumber: Long? = null
        @Synchronized
        set(it)
        {
            field = it
        }

    open fun init(request: Hello)
    {
        hello = request
        launch(CommonPool)
        {
            identifyWrapper = identifyChannel.receive()
            identify(identifyWrapper)
            job = heartbeat(identifyWrapper)
        }
    }

    open fun heartbeat(identify: Identify) = launch(CommonPool)
    {
        while (true)
        {
            heartBeat()
            delay(hello!!.heartBeatInterval)
        }
    }

    open fun handleMessage(message: OPRequest<*>)
    {
        setSequence(message.sequenceNumber)
        when (message)
        {
            is GatewayInvalid -> onGatewayInvalid(message)
            is GatewayHello -> onHello(message)
            is GatewayAck -> onAck(message)
            is GatewayReconnect -> onReconnect(message)
            is EventReady -> onReady(message)
        }
        launch(CommonPool)
        {
            Client.queue(message)
        }
    }

    open fun onGatewayInvalid(request: GatewayInvalid)
    {
        if (request.data!!)
            attemptResume()
        else
            stop()
    }

    open fun attemptResume()
    {
        val resume = Resume().apply()
        {
            this.token = Settings.token
            this.session = ready!!.sessionId
            this.sequenceNumber = getSequence()
        }
        val gwrm = GatewayResume(resume)
        session.asyncRemote.sendObject(gwrm)
    }

    open fun onHello(request: GatewayHello)
    {
        init(request.payload)
    }

    open fun onAck(request: GatewayAck)
    {
        lastAck = Date()
    }

    open fun onReconnect(request: GatewayReconnect)
    {

    }

    open fun onReady(request: EventReady)
    {
        this.ready = request.payload
    }

    open fun setSequence(s: Long?)
    {
        this.sequenceNumber = s ?: return
    }

    open fun identify(identify: Identify)
    {
        val gatewayIdentify = GatewayIdentify(identify)
        session.asyncRemote.sendObject(gatewayIdentify)
    }

    open fun isThisShard(session: Session): Boolean
    {
        return this.session == session
    }

    open fun getSequence(): Long?
    {
        return sequenceNumber
    }

    open fun getShard(): Int
    {
        return identifyWrapper.shard[0]
    }

    open fun isInitialized(): Boolean
    {
        return initialized
    }

    open fun heartBeat()
    {
        val lastack = this.lastAck
        if (lastack != null && Date().time.minus(lastack.time) > hello!!.heartBeatInterval.times(maxBeats))
        {
            stop()
            return
        }
        val gwhb = GatewayHeartbeat(sequenceNumber)
        session.asyncRemote.sendObject(gwhb)
    }

    open fun stop()
    {
        if (!session.isOpen)
            session.close()
        job.cancel()
    }

    companion object
    {
        private val identifyChannel = Channel<Identify>(10)

        fun createShard(shard: Int, shardsMax: Int)
        {
            launch(CommonPool)
            {
                Identify().apply()
                {
                    this.shard = arrayListOf(shard, shardsMax)
                    token = Settings.token
                    threshold = 50
                    identifyChannel.send(this)
                }
            }
        }
    }
}
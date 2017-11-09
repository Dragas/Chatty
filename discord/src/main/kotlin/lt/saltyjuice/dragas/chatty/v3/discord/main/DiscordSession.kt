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
    protected open val maxBeats = 3
    @get:Synchronized
    @set:Synchronized
    protected open var lastAck: Date? = null
    protected var ready: Ready? = null
    @set:Synchronized
    protected var sequenceNumber: Long? = null
    protected var resumable = true

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
        this.resumable = request.data!!
        stop()
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
        stop()
        session.close()
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

        val gatewayIdentify = if (identify is Resume)
        {
            GatewayResume(identify)
        }
        else
        {
            GatewayIdentify(identify)
        }
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

    open fun heartBeat()
    {
        val lastack = this.lastAck
        if (lastack != null && Date().time.minus(lastack.time) > hello!!.heartBeatInterval.times(maxBeats) && !session.isOpen)
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

    open fun getResume(): Resume
    {
        return Resume().apply()
        {
            this.token = identifyWrapper.token
            this.sequenceNumber = sequenceNumber
            this.session = ready?.sessionId ?: ""
        }
    }

    open fun isResumable(): Boolean
    {
        return resumable
    }

    companion object
    {
        private val identifyChannel = Channel<Identify>(10)

        @JvmStatic
        fun createShard(shard: Int, shardsMax: Int)
        {
            launch(CommonPool) { identifyChannel.send((createIdentify(shard, shardsMax))) }
        }

        @JvmStatic
        fun createIdentify(shard: Int, shardsMax: Int): Identify
        {
            return Identify().apply()
            {
                this.shard = arrayListOf(shard, shardsMax)
                token = Settings.token
                threshold = Settings.MEMBER_THRESHOLD
            }
        }

        @JvmStatic
        fun resume(resume: Resume)
        {
            launch(CommonPool) { identifyChannel.send(resume) }
        }
    }
}
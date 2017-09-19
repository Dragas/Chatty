package lt.saltyjuice.dragas.chatty.v3.discord.controller

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import lt.saltyjuice.dragas.chatty.v3.core.event.Event
import lt.saltyjuice.dragas.chatty.v3.core.route.On
import lt.saltyjuice.dragas.chatty.v3.discord.Settings
import lt.saltyjuice.dragas.chatty.v3.discord.adapter.CompressedDiscordAdapter
import lt.saltyjuice.dragas.chatty.v3.discord.adapter.DiscordAdapter
import lt.saltyjuice.dragas.chatty.v3.discord.api.Utility
import lt.saltyjuice.dragas.chatty.v3.discord.message.MessageBuilder
import lt.saltyjuice.dragas.chatty.v3.discord.message.event.EventChannelCreate
import lt.saltyjuice.dragas.chatty.v3.discord.message.event.EventGuildCreate
import lt.saltyjuice.dragas.chatty.v3.discord.message.event.EventGuildMemberAdd
import lt.saltyjuice.dragas.chatty.v3.discord.message.event.EventReady
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.*
import lt.saltyjuice.dragas.chatty.v3.discord.message.request.*
import lt.saltyjuice.dragas.chatty.v3.discord.message.response.GatewayHeartbeat
import lt.saltyjuice.dragas.chatty.v3.discord.message.response.GatewayIdentify
import lt.saltyjuice.dragas.chatty.v3.discord.message.response.OPResponse
import lt.saltyjuice.dragas.chatty.v3.websocket.controller.WebsocketConnectionController
import lt.saltyjuice.dragas.chatty.v3.websocket.event.WebSocketResponseEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.websocket.ClientEndpointConfig
import javax.websocket.Session

open class DiscordConnectionController : WebsocketConnectionController<OPRequest<*>>()
{
    override val baseClass: Class<OPRequest<*>> = OPRequest::class.java
    val shards = AtomicInteger(1)
    val currentShard = AtomicInteger(0)
    val sessions = Collections.synchronizedList(ArrayList<Session>())
    val heartbeatJobs = Collections.synchronizedList(ArrayList<Job>())

    @On(GatewayHello::class)
    fun handleHello(request: Hello)
    {
        onHello(request)
    }

    open fun onHello(request: Hello)
    {
        heartbeatJobs.add(launch(CommonPool)
        {
            val currentShard = currentShard.get()
            while (true)
            {
                onHeartbeat(currentShard)
                delay(request.heartBeatInterval)
            }
        })

        val identify = Identify().apply()
        {
            token = Settings.token
            threshold = 50
            shard = arrayListOf(currentShard.getAndIncrement(), shards.get())
        }

        handleResponse(currentShard.get() - 1, GatewayIdentify(identify))
    }

    private fun onHeartbeat(shard: Int)
    {

        var sequenceNumber: Long? = sequenceNumber.get()
        if (sequenceNumber == -1L)
            sequenceNumber = null
        handleResponse(shard, GatewayHeartbeat(sequenceNumber))
    }

    open fun handleResponse(shard: Int, response: OPResponse<*>)
    {
        val session = sessions[shard]
        handleResponse(session, response)
    }

    open fun handleResponse(session: Session, response: OPResponse<*>)
    {
        session.asyncRemote.sendObject(response)
    }

    @On(GatewayAck::class)
    fun handleAck(request: Any)
    {
        onAck()
    }

    open fun onAck()
    {

    }

    @On(GatewayInvalid::class)
    fun handleSessionInvalid(request: Boolean)
    {
        onSessionInvalid(request)
    }

    open fun onSessionInvalid(request: Boolean)
    {

    }

    @On(GatewayReconnect::class)
    fun handleReconnect(request: Any)
    {
        onReconnect()
    }

    open fun onReconnect()
    {

    }

    @On(EventReady::class)
    fun handleReady(request: Ready)
    {
        onReady(request)
    }

    open fun onReady(request: Ready)
    {
        readyEvent = request
    }

    @On(EventGuildCreate::class)
    fun handleGuildCreate(request: CreatedGuild)
    {
        onGuildCreate(request)
    }

    open fun onGuildCreate(request: CreatedGuild)
    {
        guilds[request.id] = request
        request.channels.forEach {
            channels[it.id] = it
            it.guildId = request.id
        }
    }

    @On(EventChannelCreate::class)
    fun handleChannelCreate(request: Channel)
    {
        onChannelCreate(request)
    }

    open fun onChannelCreate(request: Channel)
    {
        channels[request.id] = request
    }

    @On(EventGuildMemberAdd::class)
    open fun handleGuildMemberAdd(request: ChangedMember)
    {
        onMemberAdd(request)
    }


    open fun onMemberAdd(request: ChangedMember)
    {
        val guild = guilds[request.guildId]!!
        guild.users.add(request)
    }

    override fun getEventWrapper(request: Any): Event
    {
        return request as? OPRequest<*> ?: WebSocketResponseEvent(request)
    }

    override fun onBeforeConnect(cec: ClientEndpointConfig.Builder)
    {
        val gatewayResponse = Utility.discordAPI.gatewayInit().execute().body()!!
        uri = URI.create("${gatewayResponse.url}/?v=${Settings.API_VERSION}&encoding=${Settings.API_ENCODING}")
        shards.set(gatewayResponse.shards)
        cec
                .decoders(listOf(DiscordAdapter::class.java, CompressedDiscordAdapter::class.java))
                .encoders(listOf(DiscordAdapter::class.java))
    }

    override fun onConnect(session: Session)
    {
        sessions.add(session)
    }

    override fun handleMessage(request: OPRequest<*>)
    {
        super.handleMessage(request)
        if (request.sequenceNumber != null)
        {
            setSequenceNumber(request.sequenceNumber!!)
        }
    }



    companion object
    {
        @JvmStatic
        private val sequenceNumber: AtomicLong = AtomicLong(-1)

        @JvmStatic
        private var heartbeatJob: Job? = null

        @JvmStatic
        fun setSequenceNumber(number: Long)
        {
            sequenceNumber.set(number)
        }

        @JvmStatic
        fun destroy()
        {
            heartbeatJob?.cancel()
            heartbeatJob = null
        }

        @JvmStatic
        private lateinit var readyEvent: Ready

        @JvmStatic
        public fun isMe(anotherUser: User): Boolean
        {
            return getCurrentUserId() == anotherUser.id
        }

        @JvmStatic
        public fun getCurrentUserId(): String
        {
            return getCurrentUser().id
        }

        @JvmStatic
        public fun getCurrentUser(): User
        {
            return readyEvent.user!!
        }

        private val debugChannel = System.getenv("debug_channel_id")
        @JvmStatic
        public fun getUser(channelId: String, userId: String): Member?
        {
            val channel = channels[channelId]
            if (channel == null)
            {
                MessageBuilder().append("Warning: $channelId doesn't correspond to any channel.").send(debugChannel)
                return null
            }
            val guild = guilds[channel.guildId]
            if (guild == null)
            {
                MessageBuilder().append("Warning: channel with id ${channel.id} doesn't correspond to any guild.").send(debugChannel)
                return null
            }
            val member = guild.users.find { it.user.id == userId }
            if (member == null)
            {
                MessageBuilder().append("Warning: user with id $userId doesn't correspond to any member.").send(debugChannel)
                return null
            }
            return member
        }

        @JvmStatic
        private val guilds: HashMap<String, CreatedGuild> = HashMap()

        @JvmStatic
        private val channels: HashMap<String, Channel> = HashMap()

        @JvmStatic
        private val typingMap: ConcurrentHashMap<String, Job> = ConcurrentHashMap()

        /**
         * Starts typing to particular channel.
         */
        @JvmOverloads
        @JvmStatic
        fun startTyping(channelId: String, callback: Callback<Any> = emptyCallback)
        {
            cancelTyping(channelId)
            typingMap[channelId] = launch(CommonPool)
            {
                while (true)
                {
                    Utility.discordAPI.triggerTypingIndicator(channelId).enqueue(callback)
                    delay(10000)
                }
            }
        }

        /**
         * Stops typing to particular channel.
         */
        @JvmStatic
        fun cancelTyping(channelId: String)
        {
            typingMap.remove(channelId)?.cancel()
        }

        @JvmStatic
        val emptyCallback: Callback<Any> = object : Callback<Any>
        {
            override fun onFailure(call: Call<Any>, t: Throwable)
            {
                t.printStackTrace(System.err)
            }

            override fun onResponse(call: Call<Any>, response: Response<Any>)
            {
                println("response is successful: ${response.isSuccessful}")
            }
        }
    }
}

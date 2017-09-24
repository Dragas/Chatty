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
import lt.saltyjuice.dragas.chatty.v3.discord.main.DiscordSession
import lt.saltyjuice.dragas.chatty.v3.discord.message.MessageBuilder
import lt.saltyjuice.dragas.chatty.v3.discord.message.event.*
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.*
import lt.saltyjuice.dragas.chatty.v3.discord.message.request.OPRequest
import lt.saltyjuice.dragas.chatty.v3.websocket.controller.WebsocketConnectionController
import lt.saltyjuice.dragas.chatty.v3.websocket.event.WebSocketResponseEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import javax.websocket.ClientEndpointConfig
import javax.websocket.CloseReason
import javax.websocket.EndpointConfig
import javax.websocket.Session
import kotlinx.coroutines.experimental.channels.Channel as CoroutinesChannel

open class DiscordConnectionController : WebsocketConnectionController<OPRequest<*>>()
{
    override val baseClass: Class<OPRequest<*>> = OPRequest::class.java
    protected val shards = AtomicInteger(1)

    protected open fun createShard(shard: Int)
    {
        DiscordSession.createShard(shard, shards.get())
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
    fun handleGuildMemberAdd(request: ChangedMember)
    {
        onMemberAdd(request)
    }

    open fun onMemberAdd(request: ChangedMember)
    {
        val guild = guilds[request.guildId]!!
        guild.users.add(request)
    }

    @On(EventReady::class)
    fun handleEventReady(request: Ready)
    {
        onReady(request)
    }

    open fun onReady(request: Ready)
    {
        readyEvent = request
    }

    @On(EventGuildMemberUpdate::class)
    fun handleGuildMemberUpdate(request: ChangedMember)
    {
        onMemberUpdate(request)
    }

    open fun onMemberUpdate(request: ChangedMember)
    {
        onMemberRemove(request)
        onMemberAdd(request)
    }

    @On(EventGuildMemberRemove::class)
    fun handleGuildMemberRemove(request: ChangedMember)
    {
        onMemberRemove(request)
    }

    open fun onMemberRemove(request: ChangedMember)
    {
        guilds[request.guildId]?.users?.removeIf { it.user.id == request.user.id }
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
        repeat(gatewayResponse.shards)
        { shard ->
            createShard(shard)
            if (shard != 0)
                onConnectionInit()
        }
    }


    override fun onOpen(session: Session, config: EndpointConfig)
    {
        val discordSession = DiscordSession(session)
        sessions.add(discordSession)
        session.addMessageHandler(baseClass, discordSession::handleMessage)
    }

    override fun onClose(session: Session, reason: CloseReason)
    {
        super.onClose(session, reason)
        sessions.find { it.isThisShard(session) }?.apply()
        {
            sessions.remove(this)
            val shard = getShard()
            System.err.println("Attempting to reconnect to shard #$shard")
            DiscordSession.createShard(shard, shards.get())
            onConnectionInit()
        }
    }

    companion object
    {
        @JvmStatic
        private val sessions = Collections.synchronizedList(ArrayList<DiscordSession>())

        @JvmStatic
        @Volatile
        @set:Synchronized
        @get:Synchronized
        private var readyEvent: Ready? = null
            set(it)
            {
                it ?: return
                field = it
            }

        @JvmStatic
        @Synchronized
        public fun isMe(anotherUser: User): Boolean
        {
            return getCurrentUserId() == anotherUser.id
        }

        /**
         * Returns current user ID. Equivalent to calling [getCurrentUser]?.id
         *
         * @return null, when connection wasn't initiated
         */
        @JvmStatic
        @Synchronized
        public fun getCurrentUserId(): String?
        {
            return getCurrentUser()?.id
        }

        /**
         * Returns currently logged in user.
         *
         * @return null, when connection wasn't started
         */
        @JvmStatic
        @Synchronized
        public fun getCurrentUser(): User?
        {
            return readyEvent?.user
        }

        @JvmStatic
        @Synchronized
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
        private val guilds: ConcurrentHashMap<String, CreatedGuild> = ConcurrentHashMap<String, CreatedGuild>()

        @JvmStatic
        private val channels: ConcurrentHashMap<String, Channel> = ConcurrentHashMap()

        @JvmStatic
        private val typingMap: ConcurrentHashMap<String, Job> = ConcurrentHashMap()

        @JvmStatic
        private val debugChannel = System.getenv("debug_channel_id")

        /**
         * Starts typing to particular channel.
         */
        @JvmOverloads
        @JvmStatic
        @Synchronized
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
        @Synchronized
        fun cancelTyping(channelId: String)
        {
            typingMap.remove(channelId)?.cancel()
        }

        @JvmStatic
        @get:Synchronized
        val emptyCallback: Callback<Any> = object : Callback<Any>
            {
                override fun onFailure(call: Call<Any>, t: Throwable)
                {
                    t.printStackTrace(System.err)
                }

                override fun onResponse(call: Call<Any>, response: Response<Any>)
                {
                    //println("response is successful: ${response.isSuccessful}")
                }
            }
    }
}

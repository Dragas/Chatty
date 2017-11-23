package lt.saltyjuice.dragas.chatty.v3.discord.controller

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import lt.saltyjuice.dragas.chatty.v3.discord.Settings
import lt.saltyjuice.dragas.chatty.v3.discord.api.Utility
import lt.saltyjuice.dragas.chatty.v3.discord.main.DiscordSession
import lt.saltyjuice.dragas.chatty.v3.discord.message.builder.MessageBuilder
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.websocket.CloseReason
import javax.websocket.EndpointConfig
import javax.websocket.Session
import kotlinx.coroutines.experimental.channels.Channel as CoroutinesChannel

/**
 * Provides routes for Discord connection.
 */
open class DiscordConnectionController : AbstractDiscordConnectionController()
{
    override fun onGuildUpdate(request: Guild)
    {

    }

    override fun onChannelDelete(request: Channel)
    {
        channels.remove(request.id)
        guilds[request.guildId]?.channels?.removeIf { it.id == request.id }
    }

    override fun onGuildMemberBanRemove(request: GuildBan)
    {

    }

    override fun onGuildMemberBanAdd(request: GuildBan)
    {
        if (request.id == getCurrentUserId())
        {
            guilds.remove(request.guildId)
        }
        else
        {
            guilds[request.guildId]?.users?.removeIf { request.id == it.user.id }
        }
    }

    override fun onGuildIntegrationsUpdate(request: GuildIntegrationUpdate)
    {

    }

    override fun onGuildRoleCreate(request: RoleChanged)
    {
        guilds[request.guildId]?.roles?.add(request.role)
    }

    override fun onGuildRoleUpdate(request: RoleChanged)
    {
        onGuildRoleDelete(RoleDeleted().apply { guildId = request.guildId; roleId = request.role.id })
        onGuildRoleCreate(request)
    }

    override fun onGuildRoleDelete(request: RoleDeleted)
    {
        guilds[request.guildId]?.roles?.removeIf { it.id == request.roleId }
    }

    override fun onGuildCreate(request: CreatedGuild)
    {
        guilds[request.id] = request
        request.channels.forEach()
        {
            channels[it.id] = it
            it.guildId = request.id
        }
    }

    override fun onGuildMemberChunk(request: MemberChunk)
    {
        request.members.map()
        {
            val json = Utility.gson.toJson(it)
            val changedMember = Utility.gson.fromJson<ChangedMember>(json, ChangedMember::class.java)
            changedMember.guildId = request.guildId
            changedMember
        }.forEach(this::onMemberUpdate)
    }

    override fun onGuildDelete(request: UnavailableGuild)
    {
        if (request.unavailable == null)
        {
            guilds.remove(request.id)
        }
    }

    override fun onChannelCreate(request: Channel)
    {
        channels[request.id] = request
        guilds[request.guildId]?.channels?.add(request)
    }

    override fun onMemberAdd(request: ChangedMember)
    {
        guilds[request.guildId]?.users?.add(request)
    }

    override fun onReady(request: Ready)
    {
        readyEvent = request
    }

    override fun onMemberUpdate(request: ChangedMember)
    {
        onMemberRemove(request)
        onMemberAdd(request)
    }

    override fun onMemberRemove(request: ChangedMember)
    {
        guilds[request.guildId]?.users?.removeIf { it.user.id == request.user.id }
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
            stop()
            val shard = getShard()
            System.err.println("Attempting to reconnect to shard #$shard")
            if (isResumable())
            {
                resumeShard(getResume())
            }
            else
            {
                System.err.println("Unable to reconnect to shard #$shard. Recreating it.")
                createShard(shard)
            }
            onConnectionInit()
        }
    }

    companion object
    {
        @JvmStatic
        protected val sessions = Collections.synchronizedList(ArrayList<DiscordSession>())

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

        /**
         * Returns a user member by channel and user id. Since Members belong to guilds and channels also belong to guilds,
         * it makes sense to look for them from particular message that was sent in particular channel. Also same user
         * can be connected to multiple guilds, where this particular bot is, thus to pinpoint actual member channel
         * filtering is necessary.
         *
         * If a member is unavailable (not in cache), this method blocks current thread with a network call
         * to Discord API requests that particular member
         *
         * @param channelId id where the message had happened
         * @param userId user id
         * @return Member object from cache, if available. Otherwise a thread is blocked with a network call.
         */

        @JvmStatic
        @Synchronized
        public fun getUser(channelId: String, userId: String): Member?
        {
            if (userId.isBlank())
                return null
            val channel = channels[channelId]
            if (channel == null)
            {
                MessageBuilder(debugChannel).append("Warning: $channelId doesn't correspond to any channel.").send()
                return null
            }
            val guild = guilds[channel.guildId]
            if (guild == null)
            {
                MessageBuilder(debugChannel).append("Warning: channel with id ${channel.id} doesn't correspond to any guild.").send()
                return null
            }
            var member = guild.users.find { it.user.id == userId }
            if (member == null)
            {
                val result = Utility.discordAPI.getGuildMember(guild.id, userId).execute()
                if (result.isSuccessful)
                {
                    member = result.body()
                    guild.users.add(member!!)
                }
            }

            return member
        }

        @JvmStatic
        protected val guilds: ConcurrentHashMap<String, CreatedGuild> = ConcurrentHashMap()

        @JvmStatic
        protected val channels: ConcurrentHashMap<String, Channel> = ConcurrentHashMap()

        @JvmStatic
        protected val typingMap: ConcurrentHashMap<String, Job> = ConcurrentHashMap()

        @JvmStatic
        protected val debugChannel = System.getenv("debug_channel_id")

        /**
         * Starts typing to particular channel.
         */
        @JvmOverloads
        @JvmStatic
        @Synchronized
        fun startTyping(channelId: String, callback: Callback<Unit> = emptyCallback)
        {
            cancelTyping(channelId)
            typingMap[channelId] = launch(Unconfined)
            {
                while (true)
                {
                    Utility.discordAPI.triggerTypingIndicator(channelId).enqueue(callback)
                    delay(Settings.TYPING_DELAY)
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
        val emptyCallback: Callback<Unit> = object : Callback<Unit>
        {
            override fun onFailure(call: Call<Unit>, t: Throwable)
            {
                t.printStackTrace(System.err)
            }

            override fun onResponse(call: Call<Unit>, response: Response<Unit>)
            {
                //println("response is successful: ${response.isSuccessful}")
            }
        }
    }
}

package lt.saltyjuice.dragas.chatty.v3.discord.controller

import lt.saltyjuice.dragas.chatty.v3.core.event.Event
import lt.saltyjuice.dragas.chatty.v3.core.route.On
import lt.saltyjuice.dragas.chatty.v3.discord.Settings
import lt.saltyjuice.dragas.chatty.v3.discord.adapter.CompressedDiscordAdapter
import lt.saltyjuice.dragas.chatty.v3.discord.adapter.DiscordAdapter
import lt.saltyjuice.dragas.chatty.v3.discord.api.Utility
import lt.saltyjuice.dragas.chatty.v3.discord.main.DiscordSession
import lt.saltyjuice.dragas.chatty.v3.discord.message.event.*
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.*
import lt.saltyjuice.dragas.chatty.v3.discord.message.request.OPRequest
import lt.saltyjuice.dragas.chatty.v3.websocket.controller.WebsocketConnectionController
import lt.saltyjuice.dragas.chatty.v3.websocket.event.WebSocketResponseEvent
import java.net.URI
import java.util.concurrent.atomic.AtomicInteger
import javax.websocket.ClientEndpointConfig

/**
 * Used as an abstraction layer to separate any annotations from actual controller. Also serves as an adaption layer between
 * WebSocketConnectionController and any implementations
 */
abstract class AbstractDiscordConnectionController : WebsocketConnectionController<OPRequest<*>>()
{
    override val baseClass: Class<OPRequest<*>> = OPRequest::class.java

    /**
     * Denotes the maximum amount of shards that should be created. Received from gateway init network call.
     */
    protected val shards = AtomicInteger(1)

    protected open fun createShard(shard: Int)
    {
        DiscordSession.createShard(shard, shards.get())
    }

    protected open fun resumeShard(resume: Resume)
    {
        DiscordSession.resume(resume)
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

    override fun getEventWrapper(request: Any): Event
    {
        return request as? OPRequest<*> ?: WebSocketResponseEvent(request)
    }

    override fun onConnectionFailure(err: Exception)
    {
        super.onConnectionFailure(err)
        onConnectionInit()
    }

    @On(EventGuildCreate::class)
    fun handleGuildCreate(request: CreatedGuild)
    {
        onGuildCreate(request)
    }

    abstract fun onGuildCreate(request: CreatedGuild)

    @On(EventGuildDelete::class)
    fun handleGuildDelete(request: UnavailableGuild)
    {
        onGuildDelete(request)
    }

    abstract fun onGuildDelete(request: UnavailableGuild)

    @On(EventChannelCreate::class)
    fun handleChannelCreate(request: Channel)
    {
        onChannelCreate(request)
    }

    abstract fun onChannelCreate(request: Channel)

    @On(EventGuildMemberAdd::class)
    fun handleGuildMemberAdd(request: ChangedMember)
    {
        onMemberAdd(request)
    }

    abstract fun onMemberAdd(request: ChangedMember)

    @On(EventReady::class)
    fun handleEventReady(request: Ready)
    {
        onReady(request)
    }

    abstract fun onReady(request: Ready)

    @On(EventGuildMemberUpdate::class)
    fun handleGuildMemberUpdate(request: ChangedMember)
    {
        onMemberUpdate(request)
    }

    abstract fun onMemberUpdate(request: ChangedMember)

    @On(EventGuildMemberRemove::class)
    fun handleGuildMemberRemove(request: ChangedMember)
    {
        onMemberRemove(request)
    }

    abstract fun onMemberRemove(request: ChangedMember)

    @On(EventGuildMembersChunk::class)
    fun handleGuildMemberChunk(request: MemberChunk)
    {
        onGuildMemberChunk(request)
    }

    abstract fun onGuildMemberChunk(request: MemberChunk)

    @On(EventGuildBanRemove::class)
    fun handleGuildMemberBanRemove(request: GuildBan)
    {
        onGuildMemberBanRemove(request)
    }

    abstract fun onGuildMemberBanRemove(request: GuildBan)

    @On(EventGuildBanAdd::class)
    fun handleGuildMemberBanAdd(request: GuildBan)
    {
        onGuildMemberBanAdd(request)
    }

    abstract fun onGuildMemberBanAdd(request: GuildBan)

    @On(EventGuildIntegrationsUpdate::class)
    fun handleGuildIntegrationsUpdate(request: GuildIntegrationUpdate)
    {
        onGuildIntegrationsUpdate(request)
    }

    abstract fun onGuildIntegrationsUpdate(request: GuildIntegrationUpdate)

    @On(EventGuildRoleCreate::class)
    fun handleGuildroleCreate(request: RoleChanged)
    {
        onGuildRoleCreate(request)
    }

    abstract fun onGuildRoleCreate(request: RoleChanged)

    @On(EventGuildRoleUpdate::class)
    fun handleGuildroleUpdate(request: RoleChanged)
    {
        onGuildRoleUpdate(request)
    }

    abstract fun onGuildRoleUpdate(request: RoleChanged)

    @On(EventGuildRoleDelete::class)
    fun handleGuildroleDelete(request: RoleDeleted)
    {
        onGuildRoleDelete(request)
    }

    abstract fun onGuildRoleDelete(request: RoleDeleted)

    @On(EventChannelDelete::class)
    fun handleChannelDelete(request: Channel)
    {
        onChannelDelete(request)
    }

    abstract fun onChannelDelete(request: Channel)

    @On(EventGuildUpdate::class)
    fun handleGuildUpdate(request: Guild)
    {
        onGuildUpdate(request)
    }

    abstract fun onGuildUpdate(request: Guild)
}
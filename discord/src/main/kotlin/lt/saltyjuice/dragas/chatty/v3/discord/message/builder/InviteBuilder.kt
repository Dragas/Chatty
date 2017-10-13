package lt.saltyjuice.dragas.chatty.v3.discord.message.builder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import lt.saltyjuice.dragas.chatty.v3.discord.api.Utility
import lt.saltyjuice.dragas.chatty.v3.discord.exception.InviteBuilderException
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.Channel
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.Invite
import retrofit2.Call

open class InviteBuilder(val channelId: String) : Builder<Invite>
{
    @Expose
    @SerializedName("max_age")
    private var maxAge: Int = 86400

    @Expose
    @SerializedName("max_uses")
    private var maxUses: Int = 0

    @Expose
    @SerializedName("temporary")
    private var temporary: Boolean = false

    @Expose
    @SerializedName("unique")
    private var unique: Boolean = false

    constructor(channel: Channel) : this(channel.id)

    /**
     * 	duration of invite in seconds before expiry, or 0 for never
     */
    fun maxAge(value: Int): InviteBuilder
    {
        this.maxAge = value
        return this
    }

    /**
     * max number of uses or 0 for unlimited
     */
    fun maxUses(value: Int): InviteBuilder
    {
        this.maxUses = value
        return this
    }

    /**
     * 	whether this invite only grants temporary membership
     */
    fun temporary(value: Boolean): InviteBuilder
    {
        this.temporary = value
        return this
    }

    /**
     * if true, don't try to reuse a similar invite (useful for creating many unique one time use invites)
     */
    fun unique(value: Boolean): InviteBuilder
    {
        this.unique = value
        return this
    }


    override fun getCall(): Call<Invite>
    {
        return Utility.discordAPI.createChannelInvite(channelId, this)
    }


    @Throws(InviteBuilderException::class)
    override final fun validate()
    {
        if (maxAge < 0) throw InviteBuilderException("Max age can't be a negative value")
        if (maxUses < 0) throw InviteBuilderException("Max uses can't be a negative value")
    }
}
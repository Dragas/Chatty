package lt.saltyjuice.dragas.chatty.v3.discord.message.builder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import lt.saltyjuice.dragas.chatty.v3.discord.api.Utility
import lt.saltyjuice.dragas.chatty.v3.discord.message.api.Invite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class InviteBuilder(val channelId: String) : Builder<Invite>
{
    @Expose
    @SerializedName("max_age")
    protected var maxAge: Int? = null

    @Expose
    @SerializedName("max_uses")
    protected var maxUses: Int? = null

    @Expose
    @SerializedName("temporary")
    protected var temporary: Boolean? = null

    @Expose
    @SerializedName("unique")
    protected var unique: Boolean? = null

    fun maxAge(value: Int): InviteBuilder
    {
        this.maxAge = value
        return this
    }

    fun maxUses(value: Int): InviteBuilder
    {
        this.maxUses = value
        return this
    }

    fun temporary(value: Boolean): InviteBuilder
    {
        this.temporary = value
        return this
    }

    fun unique(value: Boolean): InviteBuilder
    {
        this.unique = value
        return this
    }

    override fun send(): Response<Invite>
    {
        return Utility.discordAPI.createChannelInvite(channelId, this).execute()
    }

    override fun sendAsync(callback: Callback<Invite>)
    {
        Utility.discordAPI.createChannelInvite(channelId, this).enqueue(this)
    }

    override fun onFailure(call: Call<Invite>, t: Throwable)
    {
        t.printStackTrace(System.err)
    }

    override fun onResponse(call: Call<Invite>, response: Response<Invite>)
    {

    }
}
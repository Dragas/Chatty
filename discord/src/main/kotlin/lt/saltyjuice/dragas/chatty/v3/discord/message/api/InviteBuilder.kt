package lt.saltyjuice.dragas.chatty.v3.discord.message.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class InviteBuilder
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
}
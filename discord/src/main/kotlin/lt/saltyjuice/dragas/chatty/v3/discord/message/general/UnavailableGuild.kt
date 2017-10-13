package lt.saltyjuice.dragas.chatty.v3.discord.message.general

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class UnavailableGuild : Guild()
{

    @SerializedName("unavailable")
    @Expose
    var unavailable: Boolean? = null
}
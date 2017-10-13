package lt.saltyjuice.dragas.chatty.v3.discord.message.general

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class Resume : Identify()
{
    @Expose
    @SerializedName("session_id")
    var session: String = ""

    @Expose
    @SerializedName("seq")
    var sequenceNumber: Long? = null
}
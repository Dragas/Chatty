package lt.saltyjuice.dragas.chatty.v3.discord.message.builder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class ChannelBuilder(id: String)
{
    @Expose
    @SerializedName("recipient_id")
    var usarId: String = id
}
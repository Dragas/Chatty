package lt.saltyjuice.dragas.chatty.v3.discord.message.general

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EmbedWrapper(@SerializedName("embed") @Expose val embed: Embed)
{
}
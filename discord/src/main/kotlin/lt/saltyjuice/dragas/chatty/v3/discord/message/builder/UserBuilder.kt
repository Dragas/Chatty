package lt.saltyjuice.dragas.chatty.v3.discord.message.builder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class UserBuilder
{
    @Expose
    @SerializedName("username")
    var username: String? = null

    @Expose
    @SerializedName("avatar")
    var avatar: String? = null


}
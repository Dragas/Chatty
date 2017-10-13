package lt.saltyjuice.dragas.chatty.v3.discord.message.general

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class Account
{
    /**
     * id of the account
     */
    @SerializedName("id")
    @Expose
    var id: String = ""
    /**
     * name of the account
     */
    @SerializedName("name")
    @Expose
    var name: String = ""
}
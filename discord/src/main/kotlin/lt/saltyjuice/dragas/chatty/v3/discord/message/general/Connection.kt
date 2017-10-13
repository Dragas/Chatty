package lt.saltyjuice.dragas.chatty.v3.discord.message.general

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The connection object that the user has attached.
 */
open class Connection
{
    /**
     * id of the connection account
     */
    @SerializedName("id")
    @Expose
    var id: String = ""
    /**
     * the username of the connection account
     */
    @SerializedName("name")
    @Expose
    var name: String = ""
    /**
     * the service of the connection (twitch, youtube)
     */
    @SerializedName("type")
    @Expose
    var type: String = ""
    /**
     * whether the connection is revoked
     */
    @SerializedName("revoked")
    @Expose
    var revoked: Boolean = false
    /**
     * an array of partial server integrations
     */
    @SerializedName("integrations")
    @Expose
    var integrations: List<Integration> = listOf()
}
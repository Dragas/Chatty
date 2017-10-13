package lt.saltyjuice.dragas.chatty.v3.discord.message.general

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

open class Integration
{
    /**
     * integration id
     */
    @SerializedName("id")
    @Expose
    var id: String = ""
    /**
     * integration name
     */
    @SerializedName("name")
    @Expose
    var name: String = ""
    /**
     * integration type (twitch, youtube, etc)
     */
    @SerializedName("type")
    @Expose
    var type: String = ""
    /**
     * is this integration enabled
     */
    @SerializedName("enabled")
    @Expose
    var enabled: Boolean = false
    /**
     * is this integration syncing
     */
    @SerializedName("syncing")
    @Expose
    var syncing: Boolean = false
    /**
     * id that this integration uses for "subscribers"
     */
    @SerializedName("role_id")
    @Expose
    var role_id: String = ""
    /**
     * the behavior of expiring subscribers
     */
    @SerializedName("expire_behavior")
    @Expose
    var expire_behavior: Int = -1
    /**
     * the grace period before expiring subscribers
     */
    @SerializedName("expire_grace_period")
    @Expose
    var expire_grace_period: Int = -1
    /**
     * user for this integration
     */
    @SerializedName("user")
    @Expose
    var user: User = User()
    /**
     * integration account information
     */
    @SerializedName("account")
    @Expose
    var account: Account = Account()
    /**
     * when this integration was last synced
     */
    @SerializedName("synced_at")
    @Expose
    var synced_at: Date = Date()
}
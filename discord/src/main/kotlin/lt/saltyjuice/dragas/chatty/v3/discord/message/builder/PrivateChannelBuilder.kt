package lt.saltyjuice.dragas.chatty.v3.discord.message.builder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import lt.saltyjuice.dragas.chatty.v3.discord.api.Utility
import lt.saltyjuice.dragas.chatty.v3.discord.exception.PrivateChannelBuilderException
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.Channel
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.User
import retrofit2.Call

/**
 * Used to create a direct channel with particular user
 */
open class PrivateChannelBuilder(id: String) : Builder<Channel>
{
    @Expose
    @SerializedName("recipient_id")
    var userId: String = id

    /**
     * a shorthand for calling `PrivateChannelBuilder(user.id)`
     */
    constructor(user: User) : this(user.id)

    override fun getCall(): Call<Channel>
    {
        return Utility.discordAPI.createChannel(this)
    }


    @Throws(PrivateChannelBuilderException::class)
    override fun validate()
    {
        if (userId.isEmpty())
            throw PrivateChannelBuilderException("User id can't be empty")
    }
}
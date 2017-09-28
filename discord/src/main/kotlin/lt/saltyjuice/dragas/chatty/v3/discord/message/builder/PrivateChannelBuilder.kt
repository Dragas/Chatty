package lt.saltyjuice.dragas.chatty.v3.discord.message.builder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import lt.saltyjuice.dragas.chatty.v3.discord.api.Utility
import lt.saltyjuice.dragas.chatty.v3.discord.exception.PrivateChannelBuilderException
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.Channel
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

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

    @Throws(IOException::class, RuntimeException::class)
    override fun send(): Response<Channel>
    {
        return Utility.discordAPI.createChannel(this).execute()
    }

    override fun sendAsync(callback: Callback<Channel>)
    {
        Utility.discordAPI.createChannel(this).enqueue(callback)
    }

    override fun onFailure(call: Call<Channel>, t: Throwable)
    {
        t.printStackTrace(System.err)
    }

    override fun onResponse(call: Call<Channel>, response: Response<Channel>)
    {

    }

    @Throws(PrivateChannelBuilderException::class)
    override fun validate()
    {
        if (userId.isEmpty())
            throw PrivateChannelBuilderException("User id can't be empty")
    }
}
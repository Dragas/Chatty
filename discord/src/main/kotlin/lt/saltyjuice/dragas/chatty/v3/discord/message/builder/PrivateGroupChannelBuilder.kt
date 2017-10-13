package lt.saltyjuice.dragas.chatty.v3.discord.message.builder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import lt.saltyjuice.dragas.chatty.v3.discord.api.Utility
import lt.saltyjuice.dragas.chatty.v3.discord.exception.PrivateChannelBuilderException
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.Channel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Create a new group DM channel with multiple users. Returns a DM channel object.
 */
open class PrivateGroupChannelBuilder : Builder<Channel>
{

    @SerializedName("nicks")
    @Expose
    private val nickMap = ConcurrentHashMap<String, String>()

    @SerializedName("access_tokens")
    private val accessTokens = Collections.synchronizedList(ArrayList<String>())

    open fun addUser(nickname: String, userId: String): PrivateGroupChannelBuilder
    {
        nickMap[userId] = nickname
        return this
    }

    /**
     * access token of user that has granted your app the gdm.join scope
     */
    open fun addAccessToken(token: String): PrivateGroupChannelBuilder
    {
        accessTokens.add(token)
        return this
    }

    @Throws(IOException::class, RuntimeException::class, PrivateChannelBuilderException::class)
    override fun send(): Response<Channel>
    {
        validate()
        return Utility.discordAPI.createGroupChannel(this).execute()
    }

    override fun onFailure(call: Call<Channel>?, t: Throwable?)
    {

    }

    override fun sendAsync(callback: Callback<Channel>)
    {
        validate()
        Utility.discordAPI.createGroupChannel(this).enqueue(callback)
    }

    @Throws(PrivateChannelBuilderException::class)
    override fun validate()
    {
        if (nickMap.size > 10)
        {
            throw PrivateChannelBuilderException("By default this endpoint is limited to 10 active group DMs. These limits are raised for whitelisted GameBridge applications.")
        }
    }

    override fun onResponse(call: Call<Channel>?, response: Response<Channel>?)
    {

    }
}
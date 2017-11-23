package lt.saltyjuice.dragas.chatty.v3.discord.message.builder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import lt.saltyjuice.dragas.chatty.v3.discord.Settings
import lt.saltyjuice.dragas.chatty.v3.discord.api.Utility
import lt.saltyjuice.dragas.chatty.v3.discord.exception.BuilderException
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.WebHook
import retrofit2.Call

open class WebHookBuilder(private val channelId: String) : ImageBuilder(), Builder<WebHook>
{
    /**
     * name of the webhook (2-32 characters)
     */
    @SerializedName("name")
    @Expose
    private var name: String = ""


    open fun name(name: String): WebHookBuilder
    {
        this.name = name
        return this
    }

    override fun getCall(): Call<WebHook>
    {
        return Utility.discordAPI.createWebHook(getImageType(), channelId, this)
    }

    override fun validate()
    {
        super.validate()
        if (!(Settings.USERNAME_MIN_LENGTH..Settings.USERNAME_MAX_LENGTH).contains(name.length))
            throw BuilderException("Permitted webhook name length is between ${Settings.USERNAME_MIN_LENGTH} and ${Settings.USERNAME_MAX_LENGTH}. Got ${name.length}")
    }
}
package lt.saltyjuice.dragas.chatty.v3.discord.message.builder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import lt.saltyjuice.dragas.chatty.v3.discord.api.Utility
import lt.saltyjuice.dragas.chatty.v3.discord.exception.EmojiBuilderException
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.Emoji
import retrofit2.Call
import java.io.File

open class EmojiBuilder(protected val guildId: String) : Builder<Emoji>, ImageBuilder()
{
    /**
     * name of the emoji
     */
    @Expose
    @SerializedName("name")
    protected var name: String = ""

    /**
     * base64 encoded 128x128 emoji image
     */
    @Expose
    @SerializedName("image")
    protected var image: String = ""

    override fun validate()
    {
        if (name.isBlank() || image.isBlank())
            throw EmojiBuilderException("name and image can't be blank")
    }

    open fun name(name: String): EmojiBuilder
    {
        this.name = name
        return this
    }

    override fun image(image: File): EmojiBuilder
    {
        super.image(image)
        this.image = getAvatar()!!
        return this
    }

    override fun getCall(): Call<Emoji>
    {
        return Utility.discordAPI.createGuildEmoji(guildId, this)
    }


}
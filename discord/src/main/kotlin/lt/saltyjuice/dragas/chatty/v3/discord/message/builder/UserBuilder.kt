package lt.saltyjuice.dragas.chatty.v3.discord.message.builder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import lt.saltyjuice.dragas.chatty.v3.discord.Settings
import lt.saltyjuice.dragas.chatty.v3.discord.api.Utility
import lt.saltyjuice.dragas.chatty.v3.discord.exception.UserBuilderException
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

/**
 * Unlike other builders, User builder does not need
 */
open class UserBuilder : Builder<User>
{
    @Expose
    @SerializedName("username")
    private var username: String? = null

    @Expose
    @SerializedName("avatar")
    private var avatar: String? = null

    private var imageType: String = ""

    open fun username(username: String): UserBuilder
    {
        this.username = username
        return this
    }

    open fun avatar(avatar: File): UserBuilder
    {
        throw UserBuilderException("Use tika to properly implement this")
        return this
    }

    override fun send(): Response<User>
    {
        validate()
        return if (avatar != null)
            Utility.discordAPI.modifyCurrentUser(imageType, this).execute()
        else
            Utility.discordAPI.modifyCurrentUser(this).execute()
    }

    override fun sendAsync(callback: Callback<User>)
    {
        validate()
        if (avatar != null)
            Utility.discordAPI.modifyCurrentUser(imageType, this).enqueue(callback)
        else
            Utility.discordAPI.modifyCurrentUser(this).enqueue(callback)
    }

    override fun onFailure(call: Call<User>, t: Throwable)
    {
        t.printStackTrace(System.err)
    }

    override fun onResponse(call: Call<User>, response: Response<User>)
    {

    }

    @Throws(UserBuilderException::class)
    final override fun validate()
    {
        if (username == null && avatar == null) throw UserBuilderException("To modify current user you should should provide at least one parameter.")
        validateUsername()
        validateImage()
    }

    private fun validateUsername()
    {
        val username = username ?: return
        if (username.length.coerceIn(Settings.USERNAME_MIN_LENGTH, Settings.USERNAME_MAX_LENGTH) != username.length)
            throw UserBuilderException("Expected username length to be between ${Settings.USERNAME_MIN_LENGTH} and ${Settings.USERNAME_MAX_LENGTH}. Got ${username.length}")
        if (username.contains(Settings.INVALID_USERNAME_REGEX))
            throw UserBuilderException("Username can't contain ${Settings.INVALID_USERNAME_CHARACTERS}. Got: $username")
        this.username = this.username!!.replace(Regex("^\\s+"), "")
        this.username = this.username!!.replace(Regex("\\s+$"), "")
    }

    private fun validateImage()
    {
        val avatar = this.avatar ?: return
        if (!imageType.startsWith("image", true))
            throw UserBuilderException("Avatars should be images. Got ${imageType}")
        if (!imageType.contains(Settings.VALID_IMAGE_TYPES))
            throw UserBuilderException("Invalid image type. Expected: ${Settings.VALID_IMAGE_TYPES_RAW}, got $imageType")

    }
}
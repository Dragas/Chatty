package lt.saltyjuice.dragas.chatty.v3.discord.message.builder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import lt.saltyjuice.dragas.chatty.v3.discord.Settings
import lt.saltyjuice.dragas.chatty.v3.discord.api.Utility
import lt.saltyjuice.dragas.chatty.v3.discord.exception.UserBuilderException
import lt.saltyjuice.dragas.chatty.v3.discord.message.general.User
import retrofit2.Call

/**
 * Unlike other builders, User builder does not need
 */
open class UserBuilder : AvatarBuilder(), Builder<User>
{
    @Expose
    @SerializedName("username")
    private var username: String? = null

    open fun username(username: String): UserBuilder
    {
        this.username = username
        return this
    }

    override fun getCall(): Call<User>
    {
        return if (getAvatar() != null)
            Utility.discordAPI.modifyCurrentUser(getImageType(), this)
        else
            Utility.discordAPI.modifyCurrentUser(this)
    }

    @Throws(UserBuilderException::class)
    final override fun validate()
    {
        if (username == null && getAvatar() == null) throw UserBuilderException("To modify current user you should should provide at least one parameter.")
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
}
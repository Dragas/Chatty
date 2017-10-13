package lt.saltyjuice.dragas.chatty.v3.discord.message.builder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import lt.saltyjuice.dragas.chatty.v3.discord.exception.UserBuilderException
import java.io.File

open class AvatarBuilder
{
    @Expose
    @SerializedName("avatar")
    private var mAvatar: String? = null

    private var mImageType: String = ""

    fun getAvatar(): String?
    {
        return mAvatar
    }

    fun getImageType(): String
    {
        return mImageType
    }

    open fun avatar(avatar: File): AvatarBuilder
    {
        throw UserBuilderException("Use tika to properly implement this")
        return this
    }
}
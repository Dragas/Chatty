package lt.saltyjuice.dragas.chatty.v3.discord.message.builder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import lt.saltyjuice.dragas.chatty.v3.discord.Settings
import lt.saltyjuice.dragas.chatty.v3.discord.exception.AvatarBuilderException
import org.apache.tika.config.TikaConfig
import org.apache.tika.metadata.Metadata
import java.io.File
import java.io.FileInputStream
import java.util.*

open class ImageBuilder
{
    @Expose
    @SerializedName("avatar")
    protected var mAvatar: String? = null

    protected var mImageType: String = ""

    protected open val validatesSize: Boolean = true

    fun getAvatar(): String?
    {
        return mAvatar
    }

    fun getImageType(): String
    {
        return mImageType
    }

    open fun validate()
    {
        validateImage()
    }

    open fun image(image: File): ImageBuilder
    {
        val filesize = image.length()
        if (validatesSize && filesize > Settings.MAX_IMAGE_SIZE)
        {
            throw AvatarBuilderException("Expected file size to be ${Settings.MAX_IMAGE_SIZE} at most. Got $filesize")
        }
        FileInputStream(image).use()
        {
            val config = TikaConfig.getDefaultConfig()
            val mediatype = config.mimeRepository.detect(it, Metadata())
            mImageType = mediatype.toString()
            val string = Base64.getEncoder().encodeToString(image.readBytes())
            mImageType = "data:$mImageType;base64,$string"
        }
        return this
    }

    @Throws(AvatarBuilderException::class)
    protected fun validateImage()
    {
        val avatar = this.getAvatar() ?: return
        if (!getImageType().startsWith("image", true))
            throw AvatarBuilderException("Avatars should be images. Got ${getImageType()}")
        if (!getImageType().contains(Settings.VALID_IMAGE_TYPES))
            throw AvatarBuilderException("Invalid image type. Expected: ${Settings.VALID_IMAGE_TYPES_RAW}, got ${getImageType()}")

    }
}
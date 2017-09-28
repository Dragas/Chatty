package lt.saltyjuice.dragas.chatty.v3.discord.message.builder

import lt.saltyjuice.dragas.chatty.v3.discord.exception.BuilderException
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/**
 * Basic interface which unites all builders under one specification.
 *
 * Since all builders are inherently different and used for different things,
 * they still have one thing in common: Being validated sent.
 * Thus instead of calling `Utility.discordAPI.something` you should use provided builder instead.
 * It knows what its doing.
 */
interface Builder<T> : Callback<T>
{
    /**
     * Sends this builder to discord API
     */
    @Throws(IOException::class, RuntimeException::class)
    fun send(): Response<T>

    /**
     * Sends this builder async, with this builder as responses callback.
     */
    fun sendAsync()
    {
        sendAsync(this)
    }

    /**
     * Sends this builder without blocking the current thread and uses the provided callback.
     */
    fun sendAsync(callback: Callback<T>)

    /**
     * Each builder should have its validation. If it doesn't pass, a [BuilderException] must be thrown.
     */
    @Throws(BuilderException::class)
    fun validate()
}
package lt.saltyjuice.dragas.chatty.v3.discord.message.builder

import lt.saltyjuice.dragas.chatty.v3.discord.exception.BuilderException
import retrofit2.Call
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
     * Sends this builder to discord API. Blocks the current thread.
     */
    @Throws(IOException::class, RuntimeException::class, BuilderException::class)
    fun send(): Response<T>
    {
        validate()
        return getCall().execute()
    }

    /**
     * Sends this builder async, with this builder as responses callback. Use this if you do not care
     * if request succeeds or not and do not care about the result.
     */
    @Throws(BuilderException::class)
    fun sendAsync()
    {
        sendAsync(this)
    }

    /**
     * Sends this builder without blocking the current thread and uses the provided callback.
     */
    @Throws(BuilderException::class)
    fun sendAsync(callback: Callback<T>)
    {
        validate()
        getCall().enqueue(callback)
    }

    /**
     * Each builder should have its validation. If it doesn't pass, a [BuilderException] must be thrown.
     */
    @Throws(BuilderException::class)
    fun validate()

    fun getCall(): Call<T>

    override fun onFailure(call: Call<T>, t: Throwable)
    {
        t.printStackTrace(System.err)
    }

    override fun onResponse(call: Call<T>, response: Response<T>)
    {

    }
}
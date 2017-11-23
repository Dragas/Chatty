package lt.saltyjuice.dragas.chatty.v3.discord.api.interceptor.account

import okhttp3.Request

/**
 * Checks if user isn't trying to change his username more than twice an hour. Throws an exception if it's being limited.
 */
open class PatchUsernameLimiter : AccountLimiter()
{
    override val shouldWaitForLimit: Boolean = false

    override fun canLimit(request: Request): Boolean
    {
        val method = request.method()
        val url = request.url().encodedPathSegments()
        val identifier = url[2]
        val target = url[3]
        val header = request.header("Content-Type")?.startsWith("image")?.not() ?: true
        return method == "PATCH" && url.size == 2 && identifier == "user" && target == "@me" && header
    }

    override fun getResetDelay(): Long
    {
        return RESET_DELAY
    }

    override fun getMaxRequests(): Int
    {
        return MAX_REQUESTS
    }

    companion object
    {
        @JvmStatic
        val RESET_DELAY = 3600000L

        @JvmStatic
        val MAX_REQUESTS = 2
    }
}
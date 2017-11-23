package lt.saltyjuice.dragas.chatty.v3.discord.api.interceptor.account

import okhttp3.Request

open class GlobalRateLimiter : AccountLimiter()
{
    override fun canLimit(request: Request): Boolean
    {
        return true
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
        val RESET_DELAY = 1000L

        @JvmStatic
        val MAX_REQUESTS = 50
    }
}
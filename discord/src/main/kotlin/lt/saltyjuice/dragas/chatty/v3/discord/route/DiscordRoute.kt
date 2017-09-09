package lt.saltyjuice.dragas.chatty.v3.discord.route

import lt.saltyjuice.dragas.chatty.v3.core.middleware.AfterMiddleware
import lt.saltyjuice.dragas.chatty.v3.core.middleware.BeforeMiddleware
import lt.saltyjuice.dragas.chatty.v3.core.route.Route
import lt.saltyjuice.dragas.chatty.v3.discord.message.request.OPRequest
import lt.saltyjuice.dragas.chatty.v3.discord.message.response.OPResponse
import lt.saltyjuice.dragas.chatty.v3.websocket.route.WebSocketRoute

open class DiscordRoute<Request : OPRequest<*>, Response : OPResponse<*>> : WebSocketRoute<Request, Response>()
{
    open class Builder<Request : OPRequest<*>, Response : OPResponse<*>> : WebSocketRoute.Builder<Request, Response>()
    {
        override fun testCallback(callback: (Route<Request, Response>, Request) -> Boolean): Builder<Request, Response>
        {
            return super.testCallback(callback) as Builder
        }

        override fun callback(callback: (Route<Request, Response>, Request) -> Unit): Builder<Request, Response>
        {
            return super.callback(callback) as Builder
        }

        override fun after(clazz: Class<out AfterMiddleware<Response>>): Builder<Request, Response>
        {
            return super.after(clazz) as Builder
        }

        override fun before(clazz: Class<out BeforeMiddleware<Request>>): Builder<Request, Response>
        {
            return super.before(clazz) as Builder
        }

        /**
         * Implementations should return a raw route object which is later used in [adapt] to add all the callbacks, middlewares, etc.
         */
        override fun returnableRoute(): DiscordRoute<Request, Response>
        {
            return DiscordRoute()
        }
    }
}
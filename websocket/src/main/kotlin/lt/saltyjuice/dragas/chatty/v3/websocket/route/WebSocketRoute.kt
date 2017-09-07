package lt.saltyjuice.dragas.chatty.v3.websocket.route

import lt.saltyjuice.dragas.chatty.v3.async.route.AsyncRoute
import lt.saltyjuice.dragas.chatty.v3.core.middleware.AfterMiddleware
import lt.saltyjuice.dragas.chatty.v3.core.middleware.BeforeMiddleware
import lt.saltyjuice.dragas.chatty.v3.core.route.Route


/**
 * WebSocketRoute wrapper for regular routes in chatty/core.
 *
 * @see Route
 */
open class WebSocketRoute<Request, Response> : AsyncRoute<Request, Response>()
{

    /**
     * Wrapper for regular routebuilder in chatty core.
     *
     * This implementation returns Websocket routes, which can be used in websocket based applications.
     */
    open class Builder<Request, Response> : AsyncRoute.Builder<Request, Response>()
    {
        override fun before(clazz: Class<out BeforeMiddleware<Request>>): Builder<Request, Response>
        {
            return super.before(clazz) as Builder
        }


        override fun after(clazz: Class<out AfterMiddleware<Response>>): Builder<Request, Response>
        {
            return super.after(clazz) as Builder
        }

        override fun description(string: String): Builder<Request, Response>
        {
            return super.description(string) as Builder
        }

        override fun adapt(route: Route<Request, Response>): WebSocketRoute<Request, Response>
        {
            return super.adapt(route) as WebSocketRoute
        }

        override fun returnableRoute(): WebSocketRoute<Request, Response>
        {
            return WebSocketRoute()
        }

        override fun testCallback(callback: (Route<Request, Response>, Request) -> Boolean): Builder<Request, Response>
        {
            return super.testCallback(callback) as Builder<Request, Response>
        }

        override fun callback(callback: (Route<Request, Response>, Request) -> Unit): Builder<Request, Response>
        {
            return super.callback(callback) as Builder<Request, Response>
        }


    }
}
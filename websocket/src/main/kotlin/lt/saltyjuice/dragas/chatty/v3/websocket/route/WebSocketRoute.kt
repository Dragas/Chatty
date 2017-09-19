package lt.saltyjuice.dragas.chatty.v3.websocket.route

import lt.saltyjuice.dragas.chatty.v3.core.route.Route
import java.lang.reflect.Method


/**
 * WebSocketRoute wrapper for regular routes in chatty/core.
 *
 * @see Route
 */
open class WebSocketRoute : Route()
{

    /**
     * Wrapper for regular routebuilder in chatty core.
     *
     * This implementation returns Websocket routes, which can be used in websocket based applications.
     */
    open class Builder : Route.Builder()
    {
        override fun description(string: String): Builder
        {
            return super.description(string) as Builder
        }

        override fun adapt(route: Route): WebSocketRoute
        {
            return super.adapt(route) as WebSocketRoute
        }

        override fun returnableRoute(): WebSocketRoute
        {
            return WebSocketRoute()
        }

        override fun testCallback(callback: Method): Builder
        {
            return super.testCallback(callback) as Builder
        }

        override fun callback(callback: Method): Builder
        {
            return super.callback(callback) as Builder
        }
    }
}
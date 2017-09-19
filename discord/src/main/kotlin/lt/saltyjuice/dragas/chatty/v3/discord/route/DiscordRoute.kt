package lt.saltyjuice.dragas.chatty.v3.discord.route

import lt.saltyjuice.dragas.chatty.v3.core.controller.Controller
import lt.saltyjuice.dragas.chatty.v3.core.event.Event
import lt.saltyjuice.dragas.chatty.v3.core.route.Route
import lt.saltyjuice.dragas.chatty.v3.websocket.route.WebSocketRoute
import java.lang.reflect.Method

open class DiscordRoute : WebSocketRoute()
{
    open class Builder : WebSocketRoute.Builder()
    {
        override fun testCallback(callback: Method): Builder
        {
            return super.testCallback(callback) as Builder
        }

        override fun callback(callback: Method): Builder
        {
            return super.callback(callback) as Builder
        }

        override fun controller(clazz: Class<out Controller>): Builder
        {
            return super.controller(clazz) as Builder
        }

        override fun type(clazz: Class<out Event>): Builder
        {
            return super.type(clazz) as Builder
        }

        override fun consume(controller: Class<out Controller>, method: Method): Builder
        {
            return super.consume(controller, method) as Builder
        }

        override fun description(string: String): Builder
        {
            return super.description(string) as Builder
        }

        override fun adapt(route: Route): DiscordRoute
        {
            return super.adapt(route) as DiscordRoute
        }

        /**
         * Implementations should return a raw route object which is later used in [adapt] to add all the callbacks, middlewares, etc.
         */
        override fun returnableRoute(): DiscordRoute
        {
            return DiscordRoute()
        }
    }
}
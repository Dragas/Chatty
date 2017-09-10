package lt.saltyjuice.dragas.chatty.v3.core.mock

import lt.saltyjuice.dragas.chatty.v3.core.event.Event
import lt.saltyjuice.dragas.chatty.v3.core.route.Router

class MockRouter : Router()
{
    override fun builder(): MockRoute.Builder
    {
        return MockRoute.Builder()
    }

    override fun consume(event: Event)
    {
        val routes = routes[event::class.java] ?: throw IllegalStateException("No routes are able to consume this")
        val route = routes.find { it.canTrigger(event) } ?: throw  IllegalStateException("No Routes are able to consume this")
        route.attemptTrigger(event)
    }
}
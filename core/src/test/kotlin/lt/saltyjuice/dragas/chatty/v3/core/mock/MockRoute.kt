package lt.saltyjuice.dragas.chatty.v3.core.mock

import lt.saltyjuice.dragas.chatty.v3.core.route.Route

class MockRoute : Route()
{
    class Builder : Route.Builder()
    {
        override fun returnableRoute(): MockRoute
        {
            return MockRoute()
        }
    }
}
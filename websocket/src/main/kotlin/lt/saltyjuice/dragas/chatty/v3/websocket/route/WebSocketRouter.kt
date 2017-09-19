package lt.saltyjuice.dragas.chatty.v3.websocket.route

import lt.saltyjuice.dragas.chatty.v3.core.route.Router


/**
 * WebSocketRouter wrapper around regular router.
 *
 * This implementation specifies that only WebSocketRouteBuilder may be used in websocket driver implementations.
 */
open class WebSocketRouter : Router()
{
    override fun builder(): WebSocketRoute.Builder
    {
        return WebSocketRoute.Builder()
    }
}
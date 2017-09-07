package lt.saltyjuice.dragas.chatty.v3.websocket.route

import kotlinx.coroutines.experimental.channels.SendChannel
import lt.saltyjuice.dragas.chatty.v3.async.route.AsyncRouter


/**
 * WebSocketRouter wrapper around regular router.
 *
 * This implementation specifies that only WebSocketRouteBuilder may be used in websocket driver implementations.
 */
open class WebSocketRouter<Request, Response>(channel: SendChannel<Response>) : AsyncRouter<Request, Response>(channel)
{
    override fun builder(): WebSocketRoute.Builder<Request, Response>
    {
        return WebSocketRoute.Builder()
    }
}
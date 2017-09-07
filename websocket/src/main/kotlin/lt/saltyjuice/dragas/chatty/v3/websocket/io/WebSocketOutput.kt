package lt.saltyjuice.dragas.chatty.v3.websocket.io

import kotlinx.coroutines.experimental.channels.Channel
import lt.saltyjuice.dragas.chatty.v3.core.io.Output

/**
 * WebSocket implementation wrapper for regular Output
 *
 * @see Output
 */
interface WebSocketOutput<Response, OutputBlock> : Output<Response, OutputBlock>
{
    /**
     * Returns response channel used by websocket.
     */
    fun getResponseChannel(): Channel<Response>
}
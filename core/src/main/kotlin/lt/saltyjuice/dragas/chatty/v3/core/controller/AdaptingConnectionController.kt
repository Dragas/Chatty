package lt.saltyjuice.dragas.chatty.v3.core.controller

import lt.saltyjuice.dragas.chatty.v3.core.adapter.Adapter

/**
 * Connection controller that features adapting features, when socket only operates in RAW data.
 */
interface AdaptingConnectionController<InputBlock, Request, Response, OutputBlock> : ConnectionController
{
    val adapter: Adapter<InputBlock, Request, Response, OutputBlock>
}
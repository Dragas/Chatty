package lt.saltyjuice.dragas.chatty.v3.core.mock.adapter

import lt.saltyjuice.dragas.chatty.v3.core.adapter.Adapter
import lt.saltyjuice.dragas.chatty.v3.core.request.FloatRequest
import lt.saltyjuice.dragas.chatty.v3.core.request.IntRequest
import lt.saltyjuice.dragas.chatty.v3.core.request.OPRequest

class OPAdapter : Adapter<String, OPRequest<*>, Any, String>()
{
    override fun deserialize(block: String): OPRequest<*>
    {
        if (block.endsWith("f"))
        {
            val floatRequest = FloatRequest()
            floatRequest.payload = block.replace("f", "").toFloat()
            return floatRequest
        }
        else
        {
            val intRequest = IntRequest()
            intRequest.payload = block.toInt()
            return intRequest
        }
    }

    override fun serialize(any: Any): String
    {
        return any.toString()
    }
}
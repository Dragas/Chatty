package lt.saltyjuice.dragas.chatty.v3.core.mock

import lt.saltyjuice.dragas.chatty.v3.core.adapter.Adapter

class MockAdapter : Adapter<String, Int, Float, String>()
{
    override fun deserialize(block: String): Int
    {
        return block.toInt()
    }

    override fun serialize(any: Float): String
    {
        return any.toString()
    }
}
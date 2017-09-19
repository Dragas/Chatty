package lt.saltyjuice.dragas.chatty.v3.discord.message.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import lt.saltyjuice.dragas.chatty.v3.core.event.Event
import lt.saltyjuice.dragas.chatty.v3.discord.CloneableData

/**
 * [OPCode] implementation, which is actually used when deserializing requests. This class' implementations specify
 * which payload does this request carry.
 */
open class OPRequest<D : Any> : OPCode(), CloneableData, Event
{
    @Expose
    @SerializedName("d")
    open var data: D? = null

    @Expose
    @SerializedName("s")
    open var sequenceNumber: Long? = null

    override val payload: D
        get()
        {
            return data ?: Any() as D
        }

    override fun clone(): OPRequest<D>
    {
        return super.clone() as OPRequest<D>
    }
}
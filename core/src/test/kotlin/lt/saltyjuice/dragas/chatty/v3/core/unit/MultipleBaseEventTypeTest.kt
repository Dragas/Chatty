package lt.saltyjuice.dragas.chatty.v3.core.unit

import kotlinx.coroutines.experimental.runBlocking
import lt.saltyjuice.dragas.chatty.v3.core.event.ConnectEvent
import lt.saltyjuice.dragas.chatty.v3.core.mock.controller.OPEventController
import lt.saltyjuice.dragas.chatty.v3.core.mock.controller.OPRequestConnectionController
import lt.saltyjuice.dragas.chatty.v3.core.mock.main.MockClient
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MultipleBaseEventTypeTest
{
    lateinit var controller: OPRequestConnectionController
    @Before
    fun initializeController()
    {
        controller = OPRequestConnectionController.getInstance()
    }

    @Test
    fun consumesFloatEvent() = runBlocking()
    {
        controller.requestChannel.send("5f")
        val response = controller.responseChannel.receive()
        Assert.assertEquals("1.0", response)
    }

    @Test
    fun consumesIntEvent() = runBlocking()
    {
        controller.requestChannel.send("5")
        val response = controller.responseChannel.receive()
        Assert.assertEquals("1", response)
    }

    companion object
    {
        @JvmStatic
        val client = MockClient(OPRequestConnectionController::class.java, OPEventController::class.java)

        @BeforeClass
        @JvmStatic
        fun init()
        {
            client.initialize()
            client.consumeEvent(ConnectEvent())
        }
    }
}
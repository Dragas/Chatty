package lt.saltyjuice.dragas.chatty.v3.core.unit

import kotlinx.coroutines.experimental.runBlocking
import lt.saltyjuice.dragas.chatty.v3.core.event.ConnectEvent
import lt.saltyjuice.dragas.chatty.v3.core.event.DisconnectEvent
import lt.saltyjuice.dragas.chatty.v3.core.mock.controller.MockConnectionController
import lt.saltyjuice.dragas.chatty.v3.core.mock.controller.MockController
import lt.saltyjuice.dragas.chatty.v3.core.mock.main.MockClient
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ConnectionControllerTest
{

    @Test
    fun consumesEvent() = runBlocking()
    {
        MockConnectionController.getInstance().requestChannel.send("5")
        val response = MockConnectionController.getInstance().responseChannel.receive()
        Assert.assertEquals("1.0", response)
    }

    companion object
    {
        @JvmStatic
        val client = MockClient(MockConnectionController::class.java, MockController::class.java)

        @JvmStatic
        @BeforeClass
        fun init()
        {
            client.initialize()
            client.consumeEvent(ConnectEvent())
        }

        @JvmStatic
        @AfterClass
        fun destroy()
        {
            client.consumeEvent(DisconnectEvent())
        }
    }
}
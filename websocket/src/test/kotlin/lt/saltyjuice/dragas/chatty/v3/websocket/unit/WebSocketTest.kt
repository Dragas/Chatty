package lt.saltyjuice.dragas.chatty.v3.websocket.unit

import kotlinx.coroutines.experimental.runBlocking
import lt.saltyjuice.dragas.chatty.v3.websocket.mock.MockClient
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class WebSocketTest
{
    @Rule
    @JvmField
    public val timeout = Timeout(1000, TimeUnit.MILLISECONDS)

    @Test
    fun canReceivesResponse() = runBlocking()
    {
        val endpoint = client.endpoint
        endpoint.handleMockMessage(5)
        client.run()
        val response = endpoint.testChannel.receive()
        Assert.assertEquals(1f, response)
    }

    companion object
    {
        @JvmStatic
        private val client = MockClient()

        @BeforeClass
        @JvmStatic
        fun init()
        {
            client.initialize()
        }
    }
}
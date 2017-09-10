package lt.saltyjuice.dragas.chatty.v3.core.unit

import lt.saltyjuice.dragas.chatty.v3.core.controller.LambdaController
import lt.saltyjuice.dragas.chatty.v3.core.event.LambdaEvent
import lt.saltyjuice.dragas.chatty.v3.core.mock.MockRouter
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RouterTest
{
    @Test
    fun consumesLambdaEvent()
    {
        val event = LambdaEvent({ println("\\o/") })
        router.consume(event)
    }

    companion object
    {
        @JvmStatic
        val router = MockRouter()

        @JvmStatic
        @BeforeClass
        fun init()
        {
            router.consume(LambdaController::class.java)
        }
    }
}
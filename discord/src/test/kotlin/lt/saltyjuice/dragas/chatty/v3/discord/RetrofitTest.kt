package lt.saltyjuice.dragas.chatty.v3.discord

import lt.saltyjuice.dragas.chatty.v3.discord.api.Utility
import lt.saltyjuice.dragas.chatty.v3.discord.main.DiscordClient
import lt.saltyjuice.dragas.chatty.v3.discord.message.request.GatewayInit
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RetrofitTest
{
    @Test
    fun retrofitCompilesCorrectly()
    {
        Utility.discordAPI
    }

    @Test
    fun clientBuildsCorrectly()
    {
        val gatewayResponse = GatewayInit()
        gatewayResponse.url = "https://google.lt"
        DiscordClient(gatewayResponse)
    }
}
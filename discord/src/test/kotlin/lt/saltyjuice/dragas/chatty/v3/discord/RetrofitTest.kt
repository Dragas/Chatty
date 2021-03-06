package lt.saltyjuice.dragas.chatty.v3.discord

import lt.saltyjuice.dragas.chatty.v3.discord.api.Utility
import lt.saltyjuice.dragas.chatty.v3.discord.controller.DiscordConnectionController
import lt.saltyjuice.dragas.chatty.v3.discord.main.DiscordClient
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
        DiscordClient(DiscordConnectionController::class.java)
    }
}
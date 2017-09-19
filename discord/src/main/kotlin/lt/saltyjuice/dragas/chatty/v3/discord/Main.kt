@file:JvmName("MainKt")

package lt.saltyjuice.dragas.chatty.v3.discord

import kotlinx.coroutines.experimental.runBlocking
import lt.saltyjuice.dragas.chatty.v3.discord.controller.DiscordConnectionController
import lt.saltyjuice.dragas.chatty.v3.discord.main.DiscordClient


fun main(args: Array<String>) = runBlocking<Unit>
{
    val discordClient = DiscordClient(DiscordConnectionController::class.java)
    discordClient.work()
}
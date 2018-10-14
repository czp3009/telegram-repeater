package com.hiczp.telegram.repeater

import mu.KotlinLogging
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update
import javax.script.CompiledScript
import javax.script.SimpleBindings

class RepeaterBot(
        private val username: String,
        private val token: String,
        private val compiledScript: CompiledScript,
        defaultBotOptions: DefaultBotOptions
) : TelegramLongPollingBot(defaultBotOptions) {
    override fun getBotUsername() = username

    override fun getBotToken() = token

    override fun onUpdateReceived(update: Update) {
        SimpleBindings().apply {
            put("logger", logger)
            put("telegramLongPollingBot", this@RepeaterBot)
            put("update", update)
        }.let {
            compiledScript.eval(it)
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}

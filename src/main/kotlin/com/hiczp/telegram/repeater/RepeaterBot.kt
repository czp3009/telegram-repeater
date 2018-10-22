package com.hiczp.telegram.repeater

import mu.KotlinLogging
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update
import javax.script.CompiledScript
import javax.script.ScriptContext

class RepeaterBot(
        private val username: String,
        private val token: String,
        private val compiledScript: CompiledScript,
        defaultBotOptions: DefaultBotOptions
) : TelegramLongPollingBot(defaultBotOptions) {
    private val engineScopeBindings = compiledScript.engine.getBindings(ScriptContext.ENGINE_SCOPE)

    init {
        engineScopeBindings.apply {
            put("logger", logger)
            put("telegramLongPollingBot", this@RepeaterBot)
            put("customVariable", config.customVariable)
        }
    }

    override fun getBotUsername() = username

    override fun getBotToken() = token

    override fun onUpdateReceived(update: Update) {
        engineScopeBindings["update"] = update
        exe.submit { compiledScript.eval(engineScopeBindings) }
    }

    companion object {
        private val logger = KotlinLogging.logger("BotScript")
    }
}

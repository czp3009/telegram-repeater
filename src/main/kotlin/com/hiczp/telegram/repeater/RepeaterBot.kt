package com.hiczp.telegram.repeater

import mu.KotlinLogging
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import javax.script.CompiledScript
import javax.script.ScriptContext
import javax.script.SimpleBindings

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
        val message = update.message
        val chatId = message.chatId
        if (message.isCommand) {
            val command = message.text.substringBefore("@")
            when (command) {
                "/enable" -> {
                    config.apply { if (disabledChatIds.remove(chatId)) flush() }
                    "Bot enabled"
                }
                "/disable" -> {
                    config.apply { if (disabledChatIds.add(chatId)) flush() }
                    "Bot disabled"
                }
                else -> "Unknown command"
            }
        } else {
            null
        }?.let {
            exe.submit { execute(SendMessage(chatId, it)) }
            return
        }

        if (chatId in config.disabledChatIds) return

        SimpleBindings().apply {
            putAll(engineScopeBindings)
            put("update", update)
        }.let {
            exe.submit { compiledScript.eval(it) }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger("BotScript")
    }
}

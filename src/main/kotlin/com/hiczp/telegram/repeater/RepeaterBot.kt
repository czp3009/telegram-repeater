package com.hiczp.telegram.repeater

import mu.KotlinLogging
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendSticker
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update

class RepeaterBot(
        private val username: String,
        private val token: String,
        defaultBotOptions: DefaultBotOptions
) : TelegramLongPollingBot(defaultBotOptions) {
    override fun getBotUsername() = username

    override fun getBotToken() = token

    override fun onUpdateReceived(update: Update) {
        val message = update.message
        val chatId = message.chatId
        val username = message.from.userName
        when {
            message.hasText() -> fun() = message.text.let {
                execute(SendMessage(chatId, it))
                logger.info { "Reply message to $username: $it" }
            }
            message.hasSticker() -> fun() = message.sticker.fileId.let {
                execute(SendSticker(chatId, it))
                logger.info { "Reply sticker to $username: ${message.sticker.setName} $it" }
            }
            else -> null
        }?.let {
            exe.submit { it() }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}

@Suppress("FunctionName")
private fun SendSticker(chatId: Long, sticker: String) =
        SendSticker().apply {
            this.chatId = chatId.toString()
            this.sticker = InputFile(sticker)
        }

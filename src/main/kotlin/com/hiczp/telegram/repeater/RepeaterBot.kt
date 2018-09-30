package com.hiczp.telegram.repeater

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
        when {
            message.hasText() -> fun() = execute(SendMessage(chatId, message.text))
            message.hasSticker() -> fun() = execute(SendSticker(chatId, message.sticker.fileId))
            else -> null
        }.let {
            if (it != null) exe.submit { it() }
        }
    }
}

@Suppress("FunctionName")
private fun SendSticker(chatId: Long, sticker: String) =
        SendSticker().apply {
            this.chatId = chatId.toString()
            this.sticker = InputFile(sticker)
        }

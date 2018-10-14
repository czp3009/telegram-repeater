import mu.KLogger
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

val logger: KLogger by bindings
val telegramLongPollingBot: TelegramLongPollingBot by bindings
val update: Update by bindings

println(update.message.text)

//val message = update.message!!
//val chatId = message.chatId!!
//val username = message.from.userName!!
//telegramLongPollingBot.run {
//    when {
//        message.hasText() -> fun() = message.text.let {
//            execute(SendMessage(chatId, it))
//            logger.info { "Reply message to $username: $it" }
//        }
//        message.hasSticker() -> fun() = message.sticker.fileId.let {
//            execute(SendSticker(chatId, it))
//            logger.info { "Reply sticker to $username: ${message.sticker.setName} $it" }
//        }
//        else -> null
//    }?.let {
//        exe.submit { it() }
//    }
//}
//
//@Suppress("FunctionName")
//fun SendSticker(chatId: Long, sticker: String) =
//        org.telegram.telegrambots.meta.api.methods.send.SendSticker().apply {
//            this.chatId = chatId.toString()
//            this.sticker = InputFile(sticker)
//        }

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import mu.KLogger
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

val tuLingAPIBaseUrl = "http://openapi.tuling123.com/openapi/api/v2"

val logger: KLogger by bindings
val telegramLongPollingBot: TelegramLongPollingBot by bindings
val update: Update by bindings
val customVariable: Map<String, String> by bindings

val message = update.message!!
val chatId = message.chatId!!
val username = message.from.userName!!

if (message.hasText()) {
    logger.info { "Received text from $username: ${message.text}" }

    val responseText = HttpClients.createMinimal().use {
        val httpPost = HttpPost(tuLingAPIBaseUrl).apply {
            entity = StringEntity(
                    TuLingRequestEntity(
                            perception = TuLingRequestEntity.Perception(inputText = TuLingRequestEntity.Perception.InputText(message.text)),
                            userInfo = TuLingRequestEntity.UserInfo(
                                    customVariable["tuLingBot.apiKey"] ?: "",
                                    customVariable["tuLingBot.userId"] ?: ""
                            )
                    ).toJson(),
                    ContentType.APPLICATION_JSON
            )
        }
        it.execute(httpPost).use { response ->
            EntityUtils.toString(response.entity, Charsets.UTF_8)
        }.let { string ->
            Gson().fromJson(string, TuLingResponseEntity::class.java)
        }
    }.let { tuLingResponseEntity ->
        tuLingResponseEntity.results?.flatMap {
            it.values.values
        }?.joinToString(separator = "\n").also {
            logger.info {
                """
                    Get response from TuLing:
                    $it
                """.trimIndent()
            }
        }
    } ?: message.text

    telegramLongPollingBot.execute(SendMessage(chatId, responseText))
}

data class TuLingRequestEntity(
        @SerializedName("reqType") val reqType: Int = 0, // 0
        @SerializedName("perception") val perception: Perception,
        @SerializedName("userInfo") val userInfo: UserInfo
) {
    data class Perception(
            @SerializedName("inputText") var inputText: InputText? = null,
            @SerializedName("inputImage") var inputImage: InputImage? = null,
            @SerializedName("selfInfo") var selfInfo: SelfInfo? = null
    ) {
        data class InputText(
                @SerializedName("text") var text: String? = null // 附近的酒店
        )

        data class SelfInfo(
                @SerializedName("location") var location: Location? = null
        ) {
            data class Location(
                    @SerializedName("city") var city: String?, // 北京
                    @SerializedName("province") var province: String?, // 北京
                    @SerializedName("street") var street: String? // 信息路
            )
        }

        data class InputImage(
                @SerializedName("url") var url: String? = null // imageUrl
        )
    }

    data class UserInfo(
            @SerializedName("apiKey") val apiKey: String,
            @SerializedName("userId") val userId: String
    )
}

fun TuLingRequestEntity.toJson() = Gson().toJson(this)

data class TuLingResponseEntity(
        @SerializedName("intent") var intent: Intent,
        @SerializedName("results") var results: List<Result>?
) {
    data class Result(
            @SerializedName("groupType") var groupType: Int, // 1
            @SerializedName("resultType") var resultType: String, // text
            @SerializedName("values") var values: Map<String, String>
    )

    data class Intent(
            @SerializedName("code") var code: Int, // 10005
            @SerializedName("intentName") var intentName: String?,
            @SerializedName("actionName") var actionName: String?,
            @SerializedName("parameters") var parameters: Map<String, String>?
    )
}

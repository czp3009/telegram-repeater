package com.hiczp.telegram.repeater

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.GsonBuilder
import org.telegram.telegrambots.bots.DefaultBotOptions
import java.io.File

typealias ProxyType = DefaultBotOptions.ProxyType

internal const val internalScriptPath = "script.kts"

val config by lazy { Config.read() }

data class Config(
        var logLevel: String = "INFO",
        var scriptPath: String = internalScriptPath,
        var username: String = "yourUsername",
        var token: String = "yourToken",
        var proxyType: ProxyType = ProxyType.NO_PROXY,
        var proxyHost: String = "localhost",
        var proxyPort: Int = 1080,
        var customVariable: Map<String, String> = HashMap()
) {
    companion object {
        @Suppress("SpellCheckingInspection")
        private val gson = GsonBuilder().setPrettyPrinting().create()
        private const val configFileName = "config.json"
        val configFile = File(configFileName)

        fun exists() = configFile.exists()

        fun read() = gson.fromJson<Config>(configFile.reader())

        fun write(config: Config = Config()) = configFile.writeText(gson.toJson(config))
    }
}

package com.hiczp.telegram.repeater

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.GsonBuilder
import org.telegram.telegrambots.bots.DefaultBotOptions
import java.io.File

typealias ProxyType = DefaultBotOptions.ProxyType

val config by lazy { Config.read() }

data class Config(
        var logLevel: String = "INFO",
        var username: String = "yourUsername",
        var token: String = "yourToken",
        var proxyType: ProxyType = ProxyType.NO_PROXY,
        var proxyHost: String = "localhost",
        var proxyPort: Int = 1080
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

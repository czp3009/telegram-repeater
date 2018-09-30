package com.hiczp.telegram.repeater

import mu.KotlinLogging
import org.apache.log4j.BasicConfigurator
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.ApiContext
import org.telegram.telegrambots.meta.TelegramBotsApi
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    //log
    BasicConfigurator.configure()

    //config
    logger.info { "Loading config file from disk" }
    if (!Config.exists()) {
        Config.write()
        logger.error { "Config file not exists, created new one. File path: ${Config.configFile.absolutePath}" }
        logger.info { "Please write correct configuration to config file and restart application" }
        exitProcess(1)
    }
    logger.info { "Config loaded" }

    //init
    logger.info { "Init ApiContext" }
    ApiContextInitializer.init()

    //proxy
    val defaultBotOptions = ApiContext.getInstance(DefaultBotOptions::class.java).apply {
        if (config.proxyType != ProxyType.NO_PROXY) {
            proxyType = config.proxyType
            proxyHost = config.proxyHost
            proxyPort = config.proxyPort
            logger.info { "Use proxy $proxyType $proxyHost:$proxyPort" }
        }
    }

    //bot
    TelegramBotsApi().apply {
        registerBot(RepeaterBot(config.username, config.token, defaultBotOptions))
    }
    logger.info { "Connect to telegram server succeed" }
}

package com.hiczp.telegram.repeater

import mu.KotlinLogging
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineBase
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.ApiContext
import org.telegram.telegrambots.meta.TelegramBotsApi
import java.io.File
import java.nio.file.Paths
import javax.script.ScriptEngineManager
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    //log
    BasicConfigurator.configure()

    //config
    logger.info { "Loading config file from disk" }
    if (Config.exists().not()) {
        Config.write()
        logger.error { "Config file not exists, created new one. File path: ${Config.configFile.absolutePath}" }
        logger.info { "Please write correct configuration to config file and restart application" }
        exitProcess(1)
    }
    logger.info { "Config loaded" }
    config.logLevel.also {
        logger.info { "Log level: $it" }
        Logger.getRootLogger().level = Level.toLevel(it)
    }
    Runtime.getRuntime().addShutdownHook(Thread {
        Config.write(config)
    })

    //script
    val scriptEngineManager = ScriptEngineManager().getEngineByName("kotlin") as KotlinJsr223JvmScriptEngineBase
    val compiledScript = File(config.scriptPath).run {
        if (exists().not()) {
            logger.warn { "External script file not exists, use default script" }
            scriptEngineManager.compile(Config::class.java.getResource(Paths.get("/", internalScriptPath).toString()).readText())
        } else {
            logger.info { "Loading external script file" }
            try {
                scriptEngineManager.compile(readText())
            } catch (e: Exception) {
                logger.error { "External script invalid: ${e.message}" }
                throw  e
            }
        }
    }

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
        maxThreads = Runtime.getRuntime().availableProcessors() * 4
    }

    //bot
    TelegramBotsApi().apply {
        registerBot(RepeaterBot(config.username, config.token, compiledScript, defaultBotOptions))
    }
    logger.info { "Connect to telegram server succeed" }
}

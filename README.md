# Telegram Repeater
人类的本质是复读机!

使用这个 Telegram Bot 来替自己复读!

# 功能
复读所有消息和表情!

# 使用
执行命令行

    java -jar telegram-repeater-0.1.0-all.jar

程序将在工作目录生成 `config.json` 文件.

填入配置后重新启动程序,

配置差不多如下

    {
      "logLevel": "INFO",
      "scriptPath": "script.kts",
      "username": "czp_bot",
      "token": "381477199:AAFRIcngHfYZ5JohphXVB3zqkqpIUJTNrdk",
      "proxyType": "SOCKS5",
      "proxyHost": "localhost",
      "proxyPort": 1080
    }

如果没有使用过 Telegram Bot, 详见 https://core.telegram.org/bots

# 自定义脚本
用户可以通过自定义脚本来进行更复杂的复读.

脚本路径在配置文件的 `scriptPath` 选项设置, 不以 `/` 开头将认为是相对路径.

脚本自身在线程池运行, 不需要考虑阻塞问题.

脚本使用 Kotlin 语言编写, 脚本上下文中有三个变量

    val logger: KLogger by bindings
    val telegramLongPollingBot: TelegramLongPollingBot by bindings
    val update: Update by bindings

`logger` 为 `slf4j` 实现类

`telegramLongPollingBot` 为 `org.telegram.telegrambots.bots.TelegramLongPollingBot` 实现类

`update` 为 `org.telegram.telegrambots.meta.api.objects.Update` 实现类

机器人 API 详见 https://github.com/rubenlagus/TelegramBots

脚本示例详见仓库的 `/script` 目录

# 构建

    ./gradlew shadowJar

# LICENSE
GPL V3

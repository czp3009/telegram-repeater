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
      "username": "czp_bot",
      "token": "381477199:AAFRIcngHfYZ5JohphXVB3zqkqpIUJTNrdk",
      "proxyType": "SOCKS5",
      "proxyHost": "localhost",
      "proxyPort": 1080
    }

如果没有使用过 Telegram Bot, 详见 https://core.telegram.org/bots

# 构建

    ./gradlew shadowJar

# LICENSE
GPL V3

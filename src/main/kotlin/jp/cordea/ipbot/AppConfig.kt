package jp.cordea.ipbot

import io.ktor.config.*

class AppConfig(config: ApplicationConfig) {
    val app = App(config.property("app.code").getString())
    val twitter = Twitter(
        config.property("twitter.token").getString(),
        config.property("twitter.rules").getList()
    )
    val rss = Rss(
        config.property("rss.interval").getString().toLong(),
        config.property("rss.urls").getList()
    )
    val line = Line(
        config.property("line.token").getString()
    )
    val db = Db(config.property("db.url").getString())

    class App(val code: String)
    class Twitter(val token: String, val rules: List<String>)
    class Rss(val interval: Long, val urls: List<String>)
    class Line(val token: String)
    class Db(val url: String)
}

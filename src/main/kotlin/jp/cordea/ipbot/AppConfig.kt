package jp.cordea.ipbot

import io.ktor.config.*

class AppConfig(config: ApplicationConfig) {
    val twitter = Twitter(config.property("twitter.rules").getList())
    val rss = Rss(
        config.property("rss.interval").getString().toLong(),
        config.property("rss.urls").getList()
    )
    val googleCloud = GoogleCloud(config.property("googleCloud.projectId").getString())
    val db = Db(config.property("db.url").getString())

    class Twitter(val rules: List<String>)
    class Rss(val interval: Long, val urls: List<String>)
    class GoogleCloud(val projectId: String)
    class Db(val url: String)
}

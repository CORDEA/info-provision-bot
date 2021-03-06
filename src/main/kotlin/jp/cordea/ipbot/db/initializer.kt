package jp.cordea.ipbot.db

import io.ktor.application.*
import jp.cordea.ipbot.db.client.DbClient

fun Application.initializeDb(): DbClient {
    val url = environment.config.property("db.url").getString()
    return DbClient(url)
}

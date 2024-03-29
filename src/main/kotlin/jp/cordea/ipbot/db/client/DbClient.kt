package jp.cordea.ipbot.db.client

import jp.cordea.ipbot.AppConfig
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.MessageDigest

class DbClient(config: AppConfig) {
    init {
        Database.connect(config.db.url, "org.sqlite.JDBC")

        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(Feeds)
            SchemaUtils.create(AuthenticatedUsers)
        }
    }

    private val digest = MessageDigest.getInstance("SHA-256")

    fun insertFeed(url: String) {
        transaction {
            addLogger(StdOutSqlLogger)

            Feeds.insertIgnore {
                it[id] = calculateId(url)
                it[latestContentId] = ""
            }
        }
    }

    fun insertLatestFeedContentId(url: String, guid: String) {
        transaction {
            addLogger(StdOutSqlLogger)

            Feeds.update({ Feeds.id eq calculateId(url) }) {
                it[latestContentId] = calculateId(guid)
            }
        }
    }

    fun findLatestFeedContentId(url: String) =
        transaction {
            Feeds.select { Feeds.id eq url }.singleOrNull()?.get(Feeds.latestContentId)
        }

    private fun calculateId(value: String) =
        digest.digest(value.toByteArray()).joinToString("") { "%02x".format(it) }
}

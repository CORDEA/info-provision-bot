package jp.cordea.ipbot.db.client

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.MessageDigest

class DbClient(url: String) {
    init {
        Database.connect(url, "org.sqlite.JDBC")

        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(RssFeeds)
        }
    }

    private val digest = MessageDigest.getInstance("SHA-256")

    fun insertRss(url: String) {
        transaction {
            addLogger(StdOutSqlLogger)

            RssFeeds.insertIgnore {
                it[id] = calculateId(url)
                it[latestContentId] = ""
            }
        }
    }

    fun insertLatestRssContentId(url: String, guid: String) {
        transaction {
            addLogger(StdOutSqlLogger)

            RssFeeds.update({ RssFeeds.id eq calculateId(url) }) {
                it[latestContentId] = calculateId(guid)
            }
        }
    }

    fun findLatestRssContentId(url: String) =
        transaction {
            RssFeeds.select { RssFeeds.id eq url }.single()[RssFeeds.latestContentId]
        }

    private fun calculateId(value: String) =
        digest.digest(value.toByteArray()).joinToString("") { "%02x".format(it) }
}

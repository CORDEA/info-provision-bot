package jp.cordea.ipbot.db.client

import org.jetbrains.exposed.sql.Table

object RssFeeds : Table() {
    val id = varchar("id", 64)
    val latestContentId = varchar("latestContentId", 64)

    override val primaryKey = PrimaryKey(id)
}

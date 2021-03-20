package jp.cordea.ipbot.db.client

import org.jetbrains.exposed.sql.Table

object AuthenticatedUsers : Table() {
    val id = varchar("id", 64)
    val observing = bool("observing")

    override val primaryKey = PrimaryKey(id)
}

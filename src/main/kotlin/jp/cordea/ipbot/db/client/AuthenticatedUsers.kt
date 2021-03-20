package jp.cordea.ipbot.db.client

import org.jetbrains.exposed.sql.Table

object AuthenticatedUsers : Table() {
    val id = varchar("id", 64)

    override val primaryKey = PrimaryKey(id)
}

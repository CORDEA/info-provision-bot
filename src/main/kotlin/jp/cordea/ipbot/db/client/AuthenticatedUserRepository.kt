package jp.cordea.ipbot.db.client

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class AuthenticatedUserRepository {
    fun findAll() = transaction {
        AuthenticatedUsers.selectAll().asIterable().map { it[AuthenticatedUsers.id] }
    }

    fun insert(id: String, observing: Boolean) {
        transaction {
            addLogger(StdOutSqlLogger)

            AuthenticatedUsers.insertIgnore {
                it[AuthenticatedUsers.id] = id
                it[AuthenticatedUsers.observing] = observing
            }
        }
    }

    fun update(id: String, observing: Boolean) {
        transaction {
            addLogger(StdOutSqlLogger)

            AuthenticatedUsers.update({ AuthenticatedUsers.id eq id }) {
                it[AuthenticatedUsers.observing] = observing
            }
        }
    }

    fun deleteAll() {
        transaction {
            addLogger(StdOutSqlLogger)

            AuthenticatedUsers.deleteAll()
        }
    }
}

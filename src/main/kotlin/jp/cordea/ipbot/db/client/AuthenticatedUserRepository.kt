package jp.cordea.ipbot.db.client

import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class AuthenticatedUserRepository() {
    fun findAll() = transaction {
        AuthenticatedUsers.selectAll().asIterable().map { it[AuthenticatedUsers.id] }
    }

    fun insert(id: String) {
        transaction {
            AuthenticatedUsers.insertIgnore {
                it[AuthenticatedUsers.id] = id
            }
        }
    }

    fun deleteAll() {
        transaction {
            AuthenticatedUsers.deleteAll()
        }
    }
}

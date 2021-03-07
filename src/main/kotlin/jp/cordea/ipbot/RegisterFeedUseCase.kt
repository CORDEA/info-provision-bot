package jp.cordea.ipbot

import jp.cordea.ipbot.db.client.DbClient

class RegisterFeedUseCase(
    private val dbClient: DbClient
) {
    fun execute(urls: List<String>) = urls.map { dbClient.insertFeed(it) }
}

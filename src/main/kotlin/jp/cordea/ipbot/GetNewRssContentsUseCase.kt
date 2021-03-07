package jp.cordea.ipbot

import jp.cordea.ipbot.db.client.DbClient
import jp.cordea.ipbot.rss.client.RssClient
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class GetNewRssContentsUseCase(
    private val rssClient: RssClient,
    private val dbClient: DbClient
) {
    fun execute(url: String) =
        rssClient.getRss(url)
            .map { response ->
                val id = dbClient.findLatestFeedContentId(url)
                response.channel.item.takeWhile { it.guid == id }
            }
            .onEach {
                it.firstOrNull()?.let { response ->
                    dbClient.insertLatestFeedContentId(url, response.guid)
                }
            }
}

package jp.cordea.ipbot.usecase

import jp.cordea.ipbot.db.client.DbClient
import jp.cordea.ipbot.rss.client.RssClient
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class GetNewRssContentsUseCase(
    private val rssClient: RssClient,
    private val dbClient: DbClient
) {
    private companion object {
        const val ITEMS_MAX = 5
    }

    fun execute(url: String) =
        rssClient.getRss(url)
            .map { response ->
                val id = dbClient.findLatestFeedContentId(url)
                if (id == null) {
                    response.channel.item
                } else {
                    response.channel.item.takeWhile { it.guid == id }
                }.take(ITEMS_MAX)
            }
            .onEach {
                it.firstOrNull()?.let { response ->
                    dbClient.insertLatestFeedContentId(url, response.guid)
                }
            }
}

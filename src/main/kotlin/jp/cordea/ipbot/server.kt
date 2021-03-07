package jp.cordea.ipbot

import io.ktor.application.*
import jp.cordea.ipbot.db.initializeDb
import jp.cordea.ipbot.rss.client.RssClient
import jp.cordea.ipbot.rss.observeRss
import jp.cordea.ipbot.twitter.observeTweets
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
fun Application.main() {
    val rssClient = RssClient()
    val dbClient = initializeDb()
    val getNewRssContentsUseCase = GetNewRssContentsUseCase(rssClient, dbClient)
    val registerFeedUseCase = RegisterFeedUseCase(dbClient)

    observeTweets()
    observeRss(registerFeedUseCase, getNewRssContentsUseCase)
}

package jp.cordea.ipbot

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import jp.cordea.ipbot.db.initializeDb
import jp.cordea.ipbot.line.initializeLineClient
import jp.cordea.ipbot.line.server.lineApi
import jp.cordea.ipbot.rss.client.RssClient
import jp.cordea.ipbot.rss.observeRss
import jp.cordea.ipbot.twitter.observeTweets
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.json.Json
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.ktor.di
import org.kodein.di.singleton
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
fun Application.main() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
    di {
        bind<RssClient>() with singleton { RssClient() }
    }
    routing {
        lineApi()
    }

    val rssClient by di().instance<RssClient>()
    val dbClient = initializeDb()
    val lineClient = initializeLineClient()
    val getNewRssContentsUseCase = GetNewRssContentsUseCase(rssClient, dbClient)
    val registerFeedUseCase = RegisterFeedUseCase(dbClient)
    val postBroadcastMessageUseCase = PostBroadcastMessageUseCase(lineClient)

    observeTweets(postBroadcastMessageUseCase)
    observeRss(registerFeedUseCase, getNewRssContentsUseCase, postBroadcastMessageUseCase)
}

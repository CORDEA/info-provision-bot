package jp.cordea.ipbot

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import jp.cordea.ipbot.db.client.DbClient
import jp.cordea.ipbot.line.client.LineClient
import jp.cordea.ipbot.line.server.lineApi
import jp.cordea.ipbot.rss.client.RssClient
import jp.cordea.ipbot.rss.observeRss
import jp.cordea.ipbot.twitter.observeTweets
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.json.Json
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.ktor.di
import org.kodein.di.provider
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
        bind<AppConfig>() with provider { AppConfig(this@main.environment.config) }

        bind<RssClient>() with singleton { RssClient() }
        bind<LineClient>() with singleton { LineClient(instance()) }
        bind<DbClient>() with singleton { DbClient(instance()) }

        bind<GetNewRssContentsUseCase>() with provider { GetNewRssContentsUseCase(instance(), instance()) }
        bind<RegisterFeedUseCase>() with provider { RegisterFeedUseCase(instance()) }
        bind<PostBroadcastMessageUseCase>() with provider { PostBroadcastMessageUseCase(instance()) }
    }
    routing {
        lineApi()
    }

    val getNewRssContentsUseCase by di().instance<GetNewRssContentsUseCase>()
    val registerFeedUseCase by di().instance<RegisterFeedUseCase>()
    val postBroadcastMessageUseCase by di().instance<PostBroadcastMessageUseCase>()

    observeTweets(postBroadcastMessageUseCase)
    observeRss(registerFeedUseCase, getNewRssContentsUseCase, postBroadcastMessageUseCase)
}

package jp.cordea.ipbot

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import jp.cordea.ipbot.db.client.AuthenticatedUserRepository
import jp.cordea.ipbot.db.client.DbClient
import jp.cordea.ipbot.line.client.LineClient
import jp.cordea.ipbot.line.server.lineApi
import jp.cordea.ipbot.rss.RssObserver
import jp.cordea.ipbot.rss.client.RssClient
import jp.cordea.ipbot.twitter.TweetObserver
import jp.cordea.ipbot.twitter.client.TweetRepository
import jp.cordea.ipbot.twitter.client.TwitterClient
import jp.cordea.ipbot.usecase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.json.Json
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.ktor.di
import org.kodein.di.provider
import org.kodein.di.singleton
import org.slf4j.event.Level
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
fun Application.main() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
    install(CallLogging) {
        level = Level.INFO
    }
    install(DefaultHeaders)
    di {
        bind<AppConfig>() with provider { AppConfig(this@main.environment.config) }

        bind<TwitterClient>() with singleton { TwitterClient(instance()) }
        bind<RssClient>() with singleton { RssClient() }
        bind<LineClient>() with singleton { LineClient(instance()) }
        bind<DbClient>() with singleton { DbClient(instance()) }

        bind<AuthenticatedUserRepository>() with singleton { AuthenticatedUserRepository() }
        bind<TweetRepository>() with singleton { TweetRepository(instance()) }

        bind<GetNewRssContentsUseCase>() with provider { GetNewRssContentsUseCase(instance(), instance()) }
        bind<RegisterFeedUseCase>() with provider { RegisterFeedUseCase(instance()) }
        bind<BroadcastPushMessagesUseCase>() with provider { BroadcastPushMessagesUseCase(instance(), instance()) }
        bind<GetAuthenticatedUsersUseCase>() with provider { GetAuthenticatedUsersUseCase(instance()) }
        bind<AddAuthenticatedUserUseCase>() with provider { AddAuthenticatedUserUseCase(instance()) }
        bind<IsAuthenticatedUserUseCase>() with provider { IsAuthenticatedUserUseCase(instance()) }
        bind<IsObservingUserExistsUseCase>() with provider { IsObservingUserExistsUseCase(instance()) }
        bind<GetTweetsUseCase>() with provider { GetTweetsUseCase(instance()) }

        bind<TweetObserver>() with provider {
            TweetObserver(
                instance(),
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
        bind<RssObserver>() with provider {
            RssObserver(
                instance(),
                instance(),
                instance(),
                instance(),
                instance()
            )
        }

        bind<Runner>() with provider { Runner(instance(), instance()) }
    }
    routing {
        lineApi()
    }

    val runner by di().instance<Runner>()
    runner.start()
    runner.resume()
}

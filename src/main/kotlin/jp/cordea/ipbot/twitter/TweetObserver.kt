package jp.cordea.ipbot.twitter

import io.ktor.application.*
import jp.cordea.ipbot.AppConfig
import jp.cordea.ipbot.PostBroadcastMessageUseCase
import jp.cordea.ipbot.line.client.TextMessage
import jp.cordea.ipbot.twitter.client.StreamRuleRequest
import jp.cordea.ipbot.twitter.client.TwitterClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import org.kodein.di.instance
import org.kodein.di.ktor.di
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

class TweetObserver(
    private val application: Application,
    private val config: AppConfig
) : CoroutineScope, Closeable {
    private val job = SupervisorJob(application.coroutineContext[Job])

    override val coroutineContext: CoroutineContext = application.coroutineContext + job

    @ExperimentalCoroutinesApi
    fun observe() {
        val client by application.di().instance<TwitterClient>()
        val postBroadcastMessageUseCase by application.di().instance<PostBroadcastMessageUseCase>()

        val rules = client
            .postStreamRules(config.twitter.rules.map { StreamRuleRequest(it) })
            .flowOn(Dispatchers.IO)
            .launchIn(this)

        client.getTweets(maxAttempts = 10)
            .map { TextMessage(it.data.text) }
            .flatMapLatest { postBroadcastMessageUseCase.execute(listOf(it)) }
            .flowOn(Dispatchers.IO)
            .launchIn(this)
    }

    override fun close() {
        job.cancel()
    }
}

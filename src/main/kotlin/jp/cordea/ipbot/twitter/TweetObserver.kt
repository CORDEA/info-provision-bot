package jp.cordea.ipbot.twitter

import io.ktor.application.*
import jp.cordea.ipbot.AppConfig
import jp.cordea.ipbot.usecase.GetAuthenticatedUsersUseCase
import jp.cordea.ipbot.usecase.SendPushMessageUseCase
import jp.cordea.ipbot.line.client.TextMessage
import jp.cordea.ipbot.twitter.client.StreamRuleRequest
import jp.cordea.ipbot.twitter.client.TwitterClient
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

class TweetObserver(
    application: Application,
    private val config: AppConfig,
    private val client: TwitterClient,
    private val sendPushMessageUseCase: SendPushMessageUseCase,
    private val getAuthenticatedUsersUseCase: GetAuthenticatedUsersUseCase
) : CoroutineScope, Closeable {
    private val job = SupervisorJob(application.coroutineContext[Job])

    override val coroutineContext: CoroutineContext = application.coroutineContext + job

    @ExperimentalCoroutinesApi
    fun observe() {
        client
            .postStreamRules(config.twitter.rules.map { StreamRuleRequest(it) })
            .flowOn(Dispatchers.IO)
            .launchIn(this)

        client.getTweets(maxAttempts = 10)
            .map { TextMessage(it.data.text) }
            .flatMapLatest { message ->
                getAuthenticatedUsersUseCase.execute()
                    .asFlow()
                    .flatMapLatest {
                        sendPushMessageUseCase.execute(it, listOf(message))
                    }
            }
            .flowOn(Dispatchers.IO)
            .launchIn(this)
    }

    override fun close() {
        job.cancelChildren()
    }
}

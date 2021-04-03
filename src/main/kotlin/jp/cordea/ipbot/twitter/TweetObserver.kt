package jp.cordea.ipbot.twitter

import io.ktor.application.*
import jp.cordea.ipbot.AppConfig
import jp.cordea.ipbot.line.client.TextMessage
import jp.cordea.ipbot.usecase.GetTweetsUseCase
import jp.cordea.ipbot.usecase.BroadcastPushMessagesUseCase
import jp.cordea.ipbot.usecase.UpdateStreamRulesUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

class TweetObserver(
    application: Application,
    private val config: AppConfig,
    private val getTweetsUseCase: GetTweetsUseCase,
    private val updateStreamRulesUseCase: UpdateStreamRulesUseCase,
    private val broadcastPushMessagesUseCase: BroadcastPushMessagesUseCase
) : CoroutineScope, Closeable {
    private val job = SupervisorJob(application.coroutineContext[Job])

    override val coroutineContext: CoroutineContext = application.coroutineContext + job

    fun setUp() {
        updateStreamRulesUseCase.execute(config.twitter.rules)
            .flowOn(Dispatchers.IO)
            .launchIn(this)
    }

    @ExperimentalCoroutinesApi
    fun observe() {
        getTweetsUseCase.execute()
            .map { TextMessage(it.data.text) }
            .flatMapLatest { broadcastPushMessagesUseCase.execute(listOf(it)) }
            .catch {
                broadcastPushMessagesUseCase.execute(
                    listOf(TextMessage(it.localizedMessage))
                )
            }
            .flowOn(Dispatchers.IO)
            .launchIn(this)
    }

    override fun close() {
        job.cancelChildren()
    }
}

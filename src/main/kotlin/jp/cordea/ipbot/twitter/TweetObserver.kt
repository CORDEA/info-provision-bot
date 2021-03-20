package jp.cordea.ipbot.twitter

import io.ktor.application.*
import jp.cordea.ipbot.AppConfig
import jp.cordea.ipbot.line.client.TextMessage
import jp.cordea.ipbot.usecase.GetTweetsUseCase
import jp.cordea.ipbot.usecase.SendPushMessagesUseCase
import jp.cordea.ipbot.usecase.UpdateStreamRulesUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

class TweetObserver(
    application: Application,
    private val config: AppConfig,
    private val getTweetsUseCase: GetTweetsUseCase,
    private val sendPushMessagesUseCase: SendPushMessagesUseCase,
    private val updateStreamRulesUseCase: UpdateStreamRulesUseCase
) : CoroutineScope, Closeable {
    private val job = SupervisorJob(application.coroutineContext[Job])

    override val coroutineContext: CoroutineContext = application.coroutineContext + job

    @ExperimentalCoroutinesApi
    fun observe() {
        updateStreamRulesUseCase.execute(config.twitter.rules)
            .flowOn(Dispatchers.IO)
            .launchIn(this)

        getTweetsUseCase.execute()
            .map { TextMessage(it.data.text) }
            .flatMapLatest { sendPushMessagesUseCase.execute(listOf(it)) }
            .catch {
                sendPushMessagesUseCase.execute(
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

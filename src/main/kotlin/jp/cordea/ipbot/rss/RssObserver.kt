package jp.cordea.ipbot.rss

import io.ktor.application.*
import jp.cordea.ipbot.AppConfig
import jp.cordea.ipbot.line.client.Message
import jp.cordea.ipbot.line.client.TextMessage
import jp.cordea.ipbot.rss.client.RssItemResponse
import jp.cordea.ipbot.usecase.GetAuthenticatedUsersUseCase
import jp.cordea.ipbot.usecase.GetNewRssContentsUseCase
import jp.cordea.ipbot.usecase.RegisterFeedUseCase
import jp.cordea.ipbot.usecase.SendPushMessagesUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

class RssObserver(
    private val config: AppConfig,
    application: Application,
    private val registerFeedUseCase: RegisterFeedUseCase,
    private val sendPushMessagesUseCase: SendPushMessagesUseCase,
    private val getNewRssContentsUseCase: GetNewRssContentsUseCase
) : CoroutineScope, Closeable {
    private val job = SupervisorJob(application.coroutineContext[Job])

    override val coroutineContext: CoroutineContext = application.coroutineContext + job

    private val urls = config.rss.urls

    fun setUp() {
        registerFeedUseCase.execute(urls)
    }

    @ExperimentalCoroutinesApi
    fun observe() {
        flow {
            emit(Unit)
            while (true) {
                emit(delay(config.rss.interval))
            }
        }
            .transform {
                urls.map {
                    emitAll(getNewRssContentsUseCase.execute(it))
                }
            }
            .map { list -> format(list).map { TextMessage(it) } }
            .flatMapLatest { sendPushMessagesUseCase.execute(it) }
            .catch {
                sendPushMessagesUseCase.execute(
                    listOf(TextMessage(it.localizedMessage))
                )
            }
            .flowOn(Dispatchers.IO)
            .launchIn(this)
    }

    private fun format(items: List<RssItemResponse>) = items.map { format(it) }

    private fun format(item: RssItemResponse) = item.title + "\n" + item.link

    override fun close() {
        job.cancelChildren()
    }
}

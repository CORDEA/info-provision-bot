package jp.cordea.ipbot.rss

import io.ktor.application.*
import jp.cordea.ipbot.*
import jp.cordea.ipbot.line.client.TextMessage
import jp.cordea.ipbot.rss.client.RssItemResponse
import jp.cordea.ipbot.usecase.GetAuthenticatedUsersUseCase
import jp.cordea.ipbot.usecase.GetNewRssContentsUseCase
import jp.cordea.ipbot.usecase.RegisterFeedUseCase
import jp.cordea.ipbot.usecase.SendPushMessageUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

class RssObserver(
    private val config: AppConfig,
    application: Application,
    private val registerFeedUseCase: RegisterFeedUseCase,
    private val sendPushMessageUseCase: SendPushMessageUseCase,
    private val getNewRssContentsUseCase: GetNewRssContentsUseCase,
    private val getAuthenticatedUsersUseCase: GetAuthenticatedUsersUseCase
) : CoroutineScope, Closeable {
    private val job = SupervisorJob(application.coroutineContext[Job])

    override val coroutineContext: CoroutineContext = application.coroutineContext + job

    @ExperimentalCoroutinesApi
    fun observe() {
        val urls = config.rss.urls
        registerFeedUseCase.execute(urls)
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
            .flatMapLatest { messages ->
                getAuthenticatedUsersUseCase.execute()
                    .asFlow()
                    .flatMapLatest { sendPushMessageUseCase.execute(it, messages) }
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

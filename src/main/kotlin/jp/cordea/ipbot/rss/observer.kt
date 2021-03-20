package jp.cordea.ipbot.rss

import io.ktor.application.*
import jp.cordea.ipbot.usecase.GetNewRssContentsUseCase
import jp.cordea.ipbot.usecase.PostBroadcastMessageUseCase
import jp.cordea.ipbot.usecase.RegisterFeedUseCase
import jp.cordea.ipbot.line.client.TextMessage
import jp.cordea.ipbot.rss.client.RssItemResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
fun Application.observeRss(
    registerFeedUseCase: RegisterFeedUseCase,
    getNewRssContentsUseCase: GetNewRssContentsUseCase,
    postBroadcastMessageUseCase: PostBroadcastMessageUseCase
) {
    val interval = environment.config.property("rss.interval").getString().toLong()
    val urls = environment.config.property("rss.urls").getList()

    registerFeedUseCase.execute(urls)
    flow {
        while (true) {
            emit(delay(interval))
        }
    }
        .transform {
            urls.map {
                emitAll(getNewRssContentsUseCase.execute(it))
            }
        }
        .map { list -> format(list).map { TextMessage(it) } }
        .flatMapLatest { postBroadcastMessageUseCase.execute(it) }
        .flowOn(Dispatchers.IO)
        .launchIn(this)
}

private fun format(items: List<RssItemResponse>) = items.map { format(it) }

private fun format(item: RssItemResponse) = item.title + "\n" + item.link

package jp.cordea.ipbot.rss

import io.ktor.application.*
import jp.cordea.ipbot.GetNewRssContentsUseCase
import jp.cordea.ipbot.RegisterFeedUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
fun Application.observeRss(
    registerFeedUseCase: RegisterFeedUseCase,
    getNewRssContentsUseCase: GetNewRssContentsUseCase
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
        .flowOn(Dispatchers.IO)
        .onEach {
            // TODO
        }
        .launchIn(this)
}

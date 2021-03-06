package jp.cordea.ipbot.rss

import io.ktor.application.*
import jp.cordea.ipbot.rss.client.RssClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
fun Application.observeRss() {
    val interval = environment.config.property("rss.interval").getString().toLong()
    val urls = environment.config.property("rss.urls").getList()
    val client = RssClient()

    flow {
        while (true) {
            emit(delay(interval))
        }
    }
        .flatMapLatest { client.getRss(urls) }
        .flowOn(Dispatchers.IO)
        .onEach {
            // TODO
        }
        .launchIn(this)
}

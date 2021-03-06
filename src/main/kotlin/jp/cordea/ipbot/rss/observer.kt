package jp.cordea.ipbot.rss

import io.ktor.application.*
import jp.cordea.ipbot.rss.client.RssClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun Application.observeRss() {
    val urls = environment.config.property("rss.urls").getList()
    val client = RssClient()

    client.getRss(urls)
        .flowOn(Dispatchers.IO)
        .onEach {
            // TODO
        }
        .launchIn(this)
}

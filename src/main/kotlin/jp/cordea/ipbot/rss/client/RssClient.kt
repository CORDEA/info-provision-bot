package jp.cordea.ipbot.rss.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.flow

class RssClient {
    private val client = HttpClient(CIO) {
        Json {
            serializer = RssSerializer()
            accept(ContentType.Text.Xml)
        }
    }

    fun getRss(urls: List<String>) = flow<RssResponse> {
        urls.forEach {
            emit(client.get(it))
        }
    }
}

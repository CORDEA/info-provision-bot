package client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import java.io.Closeable

class TwitterClient(token: String) : Closeable {
    private companion object {
        const val HOST = "https://api.twitter.com"
        const val STREAM_RULES = "2/tweets/search/stream/rules"
        const val STREAM = "2/tweets/search/stream"
    }

    private val client = HttpClient(CIO) {
        defaultRequest {
            host = HOST
            header("Authorization", "Bearer $token")
        }

        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    fun postStreamRule() {
    }

    override fun close() {
        client.close()
    }
}

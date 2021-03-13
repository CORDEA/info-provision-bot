package jp.cordea.ipbot.line.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class LineClient(token: String) {
    private companion object {
        const val HOST = "api.line.me"
        const val BROADCAST_MESSAGE = "v2/bot/message/broadcast"
    }

    private val client = HttpClient(CIO) {
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = HOST
            }
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
        }

        Json {
            serializer = KotlinxSerializer()
        }
    }

    suspend fun postBroadcastMessage(message: BroadcastMessage) {
        client.post<HttpResponse> {
            url { encodedPath = BROADCAST_MESSAGE }
            body = message
        }
    }
}

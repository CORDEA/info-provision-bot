package jp.cordea.ipbot.line.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import jp.cordea.ipbot.AppConfig

class LineClient(config: AppConfig) {
    private companion object {
        const val HOST = "api.line.me"
        const val BROADCAST_MESSAGE = "v2/bot/message/broadcast"
        const val PUSH_MESSAGE = "v2/bot/message/push"
    }

    private val client = HttpClient(CIO) {
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = HOST
            }
            header("Authorization", "Bearer ${config.line.token}")
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

    suspend fun postPushMessage(message: PushMessage) {
        client.post<HttpResponse> {
            url { encodedPath = PUSH_MESSAGE }
            body = message
        }
    }
}

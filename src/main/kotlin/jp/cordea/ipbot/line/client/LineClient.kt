package jp.cordea.ipbot.line.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import jp.cordea.ipbot.secret.SecretClient
import kotlinx.coroutines.flow.*

class LineClient private constructor(token: String) {
    private companion object {
        const val HOST = "api.line.me"
        const val BROADCAST_MESSAGE = "v2/bot/message/broadcast"
        const val PUSH_MESSAGE = "v2/bot/message/push"
    }

    class Provider(private val secretClient: SecretClient) {
        private var client: LineClient? = null

        fun provide(): Flow<LineClient> {
            val c = client
            return if (c != null) {
                flowOf(c)
            } else {
                secretClient.getSecret()
                    .map { LineClient(it.lineToken) }
                    .onEach { client = it }
                    .catch { client = null }
            }
        }
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

    fun postBroadcastMessage(message: BroadcastMessage) = flow {
        client.post<HttpResponse> {
            url { encodedPath = BROADCAST_MESSAGE }
            body = message
        }
        emit(Unit)
    }

    fun postPushMessage(message: PushMessage) = flow {
        client.post<HttpResponse> {
            url { encodedPath = PUSH_MESSAGE }
            body = message
        }
        emit(Unit)
    }
}

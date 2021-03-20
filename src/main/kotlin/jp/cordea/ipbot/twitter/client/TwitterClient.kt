package jp.cordea.ipbot.twitter.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import jp.cordea.ipbot.AppConfig
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.Closeable

class TwitterClient(config: AppConfig) : Closeable {
    private companion object {
        const val HOST = "api.twitter.com"
        const val STREAM_RULES = "2/tweets/search/stream/rules"
        const val STREAM = "2/tweets/search/stream"
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val client = HttpClient(CIO) {
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = HOST
            }
            header("Authorization", "Bearer ${config.twitter.token}")
            contentType(ContentType.Application.Json)
        }

        Json {
            serializer = KotlinxSerializer(json)
        }
    }

    suspend fun postStreamRules(rules: List<StreamRuleRequest>): StreamRulesResponse {
        val response = client.post<String> {
            url {
                encodedPath = STREAM_RULES
            }
            body = StreamRulesRequest(rules)
        }
        return runCatching {
            json.decodeFromString<StreamRulesResponse.Success>(response)
        }.getOrElse { json.decodeFromString<StreamRulesResponse.Error>(response) }
    }

    fun getTweets() = flow<Tweet> {
        client.get<HttpStatement> {
            url { encodedPath = STREAM }
        }.execute { response ->
            val channel = response.receive<ByteReadChannel>()
            do {
                val line = channel.readUTF8Line() ?: break
                emit(json.decodeFromString(line))
            } while (line.isNotBlank())
        }
    }

    override fun close() {
        client.close()
    }
}

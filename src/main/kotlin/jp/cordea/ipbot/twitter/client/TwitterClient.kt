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
import jp.cordea.ipbot.line.client.LineClient
import jp.cordea.ipbot.secret.SecretClient
import kotlinx.coroutines.flow.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.Closeable

class TwitterClient private constructor(twitterToken: String) : Closeable {
    private companion object {
        const val HOST = "api.twitter.com"
        const val STREAM_RULES = "2/tweets/search/stream/rules"
        const val STREAM = "2/tweets/search/stream"
    }

    class Provider(private val secretClient: SecretClient) {
        private var client: TwitterClient? = null

        fun provide(): Flow<TwitterClient> {
            val c = client
            return if (c != null) {
                flowOf(c)
            } else {
                secretClient.getSecret()
                    .map { TwitterClient(it.twitterToken) }
                    .onEach { client = it }
                    .catch { client = null }
            }
        }
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
            header("Authorization", "Bearer $twitterToken")
            contentType(ContentType.Application.Json)
        }

        Json {
            serializer = KotlinxSerializer(json)
        }
    }

    fun postStreamRules(rules: List<StreamRuleRequest>) = flow {
        val response = client.post<String> {
            url {
                encodedPath = STREAM_RULES
            }
            body = StreamRulesRequest(rules)
        }
        emit(
            runCatching {
                json.decodeFromString<StreamRulesResponse.Success>(response)
            }.getOrElse { json.decodeFromString<StreamRulesResponse.Error>(response) }
        )
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

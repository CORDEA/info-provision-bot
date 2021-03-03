package client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.Closeable

class TwitterClient(token: String) : Closeable {
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
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
        }

        install(JsonFeature) {
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

    override fun close() {
        client.close()
    }
}

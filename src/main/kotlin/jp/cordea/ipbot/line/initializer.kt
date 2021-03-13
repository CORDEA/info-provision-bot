package jp.cordea.ipbot.line

import io.ktor.application.*
import jp.cordea.ipbot.line.client.LineClient

fun Application.initializeLineClient(): LineClient {
    val token = environment.config.property("line.token").getString()
    return LineClient(token)
}

package jp.cordea.ipbot.line.server

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.lineApi() {
    route("line") {
        post("/webhook") {
            val signature = requireNotNull(call.request.headers["x-line-signature"])
            val event = call.receive<Webhook>()
            call.respond(HttpStatusCode.OK)
        }
    }
}

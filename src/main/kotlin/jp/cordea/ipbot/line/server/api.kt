package jp.cordea.ipbot.line.server

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

fun Routing.lineApi() {
    val handler by di().instance<MessageHandler>()
    route("line") {
        post("/webhook") {
            val signature = requireNotNull(call.request.headers["x-line-signature"])
            val event = call.receive<Webhook>()
            event.events.forEach { handler.handle(it) }
            call.respond(HttpStatusCode.OK)
        }
    }
}

package jp.cordea.ipbot.line.server

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.kodein.di.instance
import org.kodein.di.ktor.di

@ExperimentalCoroutinesApi
fun Routing.lineApi() {
    val handler by di().instance<MessageHandler>()
    route("line") {
        post("/webhook") {
            val signature = requireNotNull(call.request.headers["x-line-signature"])
            if (!handler.verifySignature(signature, call.receiveText())) {
                call.respond(HttpStatusCode.OK)
                return@post
            }
            val event = call.receive<Webhook>()
            call.respond(HttpStatusCode.OK)
            event.events.forEach { handler.handle(it) }
        }
    }
}

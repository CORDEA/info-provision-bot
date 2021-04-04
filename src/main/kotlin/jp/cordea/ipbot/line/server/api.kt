package jp.cordea.ipbot.line.server

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import jp.cordea.ipbot.usecase.GetSecretUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.kodein.di.instance
import org.kodein.di.ktor.di

@ExperimentalCoroutinesApi
fun Routing.lineApi() {
    val handler by di().instance<MessageHandler>()
    val getSecretUseCase by di().instance<GetSecretUseCase>()
    route("line") {
        post("/webhook") {
            val signature = requireNotNull(call.request.headers["x-line-signature"])
            val text = call.receiveText()
            val event = call.receive<Webhook>()
            call.respond(HttpStatusCode.OK)
            getSecretUseCase.execute()
                .flowOn(Dispatchers.IO)
                .onEach { secret ->
                    if (handler.verifySignature(signature, text, secret.lineSecret)) {
                        event.events.forEach { handler.handle(it, secret.appCode) }
                    }
                }
                .launchIn(this)
        }
    }
}

package jp.cordea.ipbot.line.server

import jp.cordea.ipbot.AppConfig
import jp.cordea.ipbot.Runner
import jp.cordea.ipbot.line.client.TextMessage
import jp.cordea.ipbot.usecase.AddAuthenticatedUserUseCase
import jp.cordea.ipbot.usecase.IsAuthenticatedUserUseCase
import jp.cordea.ipbot.usecase.IsObservingUserExistsUseCase
import jp.cordea.ipbot.usecase.UpdateObservationStatusUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@ExperimentalCoroutinesApi
class MessageHandler(
    private val runner: Runner,
    private val appConfig: AppConfig,
    private val isAuthenticatedUserUseCase: IsAuthenticatedUserUseCase,
    private val addAuthenticatedUserUseCase: AddAuthenticatedUserUseCase,
    private val isObservingUserExistsUseCase: IsObservingUserExistsUseCase,
    private val updateObservationStatusUseCase: UpdateObservationStatusUseCase
) {
    fun verifySignature(signature: String, body: String): Boolean {
        val key = SecretKeySpec(appConfig.line.secret.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256").apply { init(key) }
        return signature == Base64.getEncoder().encodeToString(mac.doFinal(body.toByteArray()))
    }

    fun handle(event: Event) {
        when (event) {
            is MessageEvent -> handleMessageEvent(event)
            else -> {
            }
        }
    }

    private fun handleMessageEvent(event: MessageEvent) {
        val id = when (val source = event.source) {
            is UserEvent -> source.userId
            is GroupEvent -> source.groupId
            is RoomEvent -> source.roomId
        }
        val text = (event.message as? TextMessage)?.text ?: return
        val isAuthenticated = isAuthenticatedUserUseCase.execute(id)
        if (!isAuthenticated) {
            tryAuthenticate(id, text)
            return
        }
        when (text) {
            "resume" -> resume(id)
            "ping" -> ping()
            "pause" -> pause(id)
        }
    }

    private fun tryAuthenticate(id: String, text: String) {
        if (appConfig.app.code != text) {
            return
        }
        addAuthenticatedUserUseCase.execute(id)
    }

    private fun resume(id: String) {
        updateObservationStatusUseCase.execute(id, true)
        runner.resume()
    }

    private fun ping() {
        // TODO
    }

    private fun pause(id: String) {
        updateObservationStatusUseCase.execute(id, false)
        if (!isObservingUserExistsUseCase.execute()) {
            runner.pause()
        }
    }
}

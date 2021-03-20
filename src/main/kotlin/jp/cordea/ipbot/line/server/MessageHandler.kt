package jp.cordea.ipbot.line.server

import jp.cordea.ipbot.AppConfig
import jp.cordea.ipbot.Runner
import jp.cordea.ipbot.line.client.TextMessage
import jp.cordea.ipbot.usecase.AddAuthenticatedUserUseCase
import jp.cordea.ipbot.usecase.IsAuthenticatedUserExistsUseCase
import jp.cordea.ipbot.usecase.IsAuthenticatedUserUseCase

class MessageHandler(
    private val runner: Runner,
    private val appConfig: AppConfig,
    private val isAuthenticatedUserUseCase: IsAuthenticatedUserUseCase,
    private val addAuthenticatedUserUseCase: AddAuthenticatedUserUseCase,
    private val isAuthenticatedUserExistsUseCase: IsAuthenticatedUserExistsUseCase
) {
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
            "resume" -> resume()
            "ping" -> ping()
            "pause" -> pause()
        }
    }

    private fun tryAuthenticate(id: String, text: String) {
        if (appConfig.app.code != text) {
            return
        }
        addAuthenticatedUserUseCase.execute(id)
    }

    private fun resume() {
        if (isAuthenticatedUserExistsUseCase.execute()) {
            runner.resume()
        }
    }

    private fun ping() {
        // TODO
    }

    private fun pause() {
        runner.pause()
    }
}

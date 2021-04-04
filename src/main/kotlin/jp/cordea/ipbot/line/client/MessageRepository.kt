package jp.cordea.ipbot.line.client

import kotlinx.coroutines.flow.flatMapLatest

class MessageRepository(
    private val clientProvider: LineClient.Provider
) {
    fun broadcast(messages: List<Message>) = clientProvider.provide()
        .flatMapLatest { it.postBroadcastMessage(BroadcastMessage(messages, false)) }

    fun push(targetId: String, messages: List<Message>) = clientProvider.provide()
        .flatMapLatest { it.postPushMessage(PushMessage(targetId, messages, false)) }
}

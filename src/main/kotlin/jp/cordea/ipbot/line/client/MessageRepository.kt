package jp.cordea.ipbot.line.client

import kotlinx.coroutines.flow.flow

class MessageRepository(
    private val client: LineClient
) {
    fun broadcast(messages: List<Message>) = flow {
        client.postBroadcastMessage(BroadcastMessage(messages, false))
        emit(Unit)
    }

    fun push(targetId: String, messages: List<Message>) = flow {
        client.postPushMessage(PushMessage(targetId, messages, false))
        emit(Unit)
    }
}

package jp.cordea.ipbot

import jp.cordea.ipbot.line.client.LineClient
import jp.cordea.ipbot.line.client.Message
import jp.cordea.ipbot.line.client.PushMessage
import kotlinx.coroutines.flow.flow

class SendPushMessageUseCase(
    private val client: LineClient
) {
    fun execute(targetId: String, messages: List<Message>) = flow {
        emit(client.postPushMessage(PushMessage(targetId, messages, false)))
    }
}

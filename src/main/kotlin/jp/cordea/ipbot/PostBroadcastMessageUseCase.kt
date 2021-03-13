package jp.cordea.ipbot

import jp.cordea.ipbot.line.client.BroadcastMessage
import jp.cordea.ipbot.line.client.LineClient
import jp.cordea.ipbot.line.client.Message
import kotlinx.coroutines.flow.flow

class PostBroadcastMessageUseCase(
    private val lineClient: LineClient
) {
    fun execute(messages: List<Message>) =
        flow { emit(lineClient.postBroadcastMessage(BroadcastMessage(messages, false))) }
}

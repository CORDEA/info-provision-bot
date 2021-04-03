package jp.cordea.ipbot.usecase

import jp.cordea.ipbot.line.client.Message
import jp.cordea.ipbot.line.client.MessageRepository

class SendPushMessagesUseCase(
    private val messageRepository: MessageRepository
) {
    fun execute(id: String, messages: List<Message>) = messageRepository.push(id, messages)
}

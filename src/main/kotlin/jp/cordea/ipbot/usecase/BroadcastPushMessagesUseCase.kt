package jp.cordea.ipbot.usecase

import jp.cordea.ipbot.db.client.AuthenticatedUserRepository
import jp.cordea.ipbot.line.client.Message
import jp.cordea.ipbot.line.client.MessageRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest

class BroadcastPushMessagesUseCase(
    private val messageRepository: MessageRepository,
    private val authenticatedUserRepository: AuthenticatedUserRepository
) {
    @ExperimentalCoroutinesApi
    fun execute(messages: List<Message>) =
        authenticatedUserRepository.findAll()
            .asFlow()
            .filter { it.observing }
            .flatMapLatest { messageRepository.push(it.id, messages) }
}

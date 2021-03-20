package jp.cordea.ipbot.usecase

import jp.cordea.ipbot.db.client.AuthenticatedUserRepository

class IsObservingUserExistsUseCase(
    private val repository: AuthenticatedUserRepository
) {
    fun execute() = repository.findAll().any { it.observing }
}

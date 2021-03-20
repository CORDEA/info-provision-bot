package jp.cordea.ipbot.usecase

import jp.cordea.ipbot.db.client.AuthenticatedUserRepository

class IsAuthenticatedUserExistsUseCase(
    private val repository: AuthenticatedUserRepository
) {
    fun execute() = repository.findAll().isNotEmpty()
}

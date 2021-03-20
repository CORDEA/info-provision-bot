package jp.cordea.ipbot.usecase

import jp.cordea.ipbot.db.client.AuthenticatedUserRepository

class GetAuthenticatedUsersUseCase(
    private val repository: AuthenticatedUserRepository
) {
    fun execute() = repository.findAll()
}

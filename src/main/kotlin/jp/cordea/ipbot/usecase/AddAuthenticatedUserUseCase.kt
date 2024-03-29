package jp.cordea.ipbot.usecase

import jp.cordea.ipbot.db.client.AuthenticatedUserRepository

class AddAuthenticatedUserUseCase(
    private val repository: AuthenticatedUserRepository
) {
    fun execute(id: String) = repository.insert(id, false)
}

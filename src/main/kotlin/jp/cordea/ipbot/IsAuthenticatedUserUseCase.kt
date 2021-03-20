package jp.cordea.ipbot

import jp.cordea.ipbot.db.client.AuthenticatedUserRepository

class IsAuthenticatedUserUseCase(
    private val repository: AuthenticatedUserRepository
) {
    fun execute(id: String) = repository.findAll().contains(id)
}

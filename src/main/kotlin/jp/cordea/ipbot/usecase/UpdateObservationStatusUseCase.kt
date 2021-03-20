package jp.cordea.ipbot.usecase

import jp.cordea.ipbot.db.client.AuthenticatedUserRepository

class UpdateObservationStatusUseCase(
    private val repository: AuthenticatedUserRepository
) {
    fun execute(id: String, observing: Boolean) = repository.update(id, observing)
}

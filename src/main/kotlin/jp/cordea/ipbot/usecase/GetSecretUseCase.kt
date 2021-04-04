package jp.cordea.ipbot.usecase

import jp.cordea.ipbot.secret.SecretRepository

class GetSecretUseCase(private val repository: SecretRepository) {
    fun execute() = repository.find()
}

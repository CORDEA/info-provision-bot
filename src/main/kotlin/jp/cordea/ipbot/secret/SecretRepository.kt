package jp.cordea.ipbot.secret

class SecretRepository(
    private val client: SecretClient
) {
    fun find() = client.getSecret()
}

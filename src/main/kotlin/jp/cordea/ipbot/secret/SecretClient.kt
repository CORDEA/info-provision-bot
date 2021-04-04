package jp.cordea.ipbot.secret

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName
import jp.cordea.ipbot.AppConfig
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class SecretClient(
    private val appConfig: AppConfig
) {
    private val client = SecretManagerServiceClient.create()

    fun getSecret() = combine(
        findValue(Secret.KEY_APP_CODE),
        findValue(Secret.KEY_TWITTER_TOKEN),
        findValue(Secret.KEY_LINE_TOKEN),
        findValue(Secret.KEY_LINE_SECRET),
        ::Secret
    )

    private fun findValue(key: String) = flow<String> {
        val versionName = SecretVersionName.of(appConfig.googleCloud.projectId, key, "latest")
        val response = client.accessSecretVersion(versionName)
        emit(response.payload.data.toStringUtf8())
    }
}

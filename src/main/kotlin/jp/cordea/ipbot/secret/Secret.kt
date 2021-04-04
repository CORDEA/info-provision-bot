package jp.cordea.ipbot.secret

data class Secret(
    val appCode: String,
    val twitterToken: String,
    val lineToken: String,
    val lineSecret: String
) {
    companion object {
        const val KEY_APP_CODE = "app_code"
        const val KEY_TWITTER_TOKEN = "twitter_token"
        const val KEY_LINE_TOKEN = "line_token"
        const val KEY_LINE_SECRET = "line_secret"
    }
}

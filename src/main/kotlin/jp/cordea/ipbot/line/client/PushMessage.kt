package jp.cordea.ipbot.line.client

import kotlinx.serialization.Serializable

@Serializable
data class PushMessage(
    val to: String,
    val messages: List<Message>,
    val notificationDisabled: Boolean
)

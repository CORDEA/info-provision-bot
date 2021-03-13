package jp.cordea.ipbot.line.client

import kotlinx.serialization.Serializable

@Serializable
data class BroadcastMessage(
    val messages: List<Message>,
    val notificationDisabled: Boolean
)

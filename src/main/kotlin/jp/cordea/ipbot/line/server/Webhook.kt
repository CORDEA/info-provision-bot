package jp.cordea.ipbot.line.server

import kotlinx.serialization.Serializable

@Serializable
class Webhook(
    val destination: String,
    val events: List<Event>
)

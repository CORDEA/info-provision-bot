package jp.cordea.ipbot.line.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Message

@Serializable
@SerialName("text")
data class TextMessage(
    val text: String
) : Message()

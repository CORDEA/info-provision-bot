package jp.cordea.ipbot.line.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Message

@Serializable
@SerialName("text")
class TextMessage(
    val text: String
) : Message()

package jp.cordea.ipbot.line.server

import jp.cordea.ipbot.line.client.Message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Event

@Serializable
@SerialName("user")
class UserEvent(val userId: String)

@Serializable
@SerialName("group")
class GroupEvent(val groupId: String, val userId: String?)

@Serializable
@SerialName("room")
class RoomEvent(val roomId: String, val userId: String?)

@Serializable
@SerialName("message")
class MessageEvent(val replyToken: String, val message: Message)

package jp.cordea.ipbot.line.server

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Source

@Serializable
@SerialName("user")
class UserEvent(val userId: String) : Source()

@Serializable
@SerialName("group")
class GroupEvent(val groupId: String, val userId: String?) : Source()

@Serializable
@SerialName("room")
class RoomEvent(val roomId: String, val userId: String?) : Source()

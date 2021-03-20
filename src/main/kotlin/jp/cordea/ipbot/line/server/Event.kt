package jp.cordea.ipbot.line.server

import jp.cordea.ipbot.line.client.Message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Event

@Serializable
@SerialName("message")
class MessageEvent(val replyToken: String, val message: Message, val source: Source) : Event()

@Serializable
@SerialName("follow")
class FollowEvent(val replyToken: String, val source: Source) : Event()

@Serializable
@SerialName("unfollow")
class UnfollowEvent(val replyToken: String, val source: Source) : Event()

@Serializable
@SerialName("join")
class JoinEvent(val replyToken: String, val source: Source) : Event()

@Serializable
@SerialName("leave")
class LeaveEvent(val replyToken: String, val source: Source) : Event()

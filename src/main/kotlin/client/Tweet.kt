package client

import kotlinx.serialization.Serializable

@Serializable
data class Tweet(
    val data: TweetData
)

@Serializable
data class TweetData(
    val id: String,
    val text: String
)

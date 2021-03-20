package jp.cordea.ipbot.twitter.client

class TweetRepository(
    private val client: TwitterClient
) {
    fun findAll() = client.getTweets()
}

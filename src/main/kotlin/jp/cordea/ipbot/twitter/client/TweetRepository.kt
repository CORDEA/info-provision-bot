package jp.cordea.ipbot.twitter.client

import kotlinx.coroutines.flow.flatMapLatest

class TweetRepository(
    private val clientProvider: TwitterClient.Provider
) {
    fun findAll() = clientProvider.provide().flatMapLatest { it.getTweets() }
}

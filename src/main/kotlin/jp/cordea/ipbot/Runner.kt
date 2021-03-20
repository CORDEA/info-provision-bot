package jp.cordea.ipbot

import jp.cordea.ipbot.rss.RssObserver
import jp.cordea.ipbot.twitter.TweetObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi

class Runner(
    private val rssObserver: RssObserver,
    private val tweetObserver: TweetObserver
) {
    fun start() {
        rssObserver.setUp()
        tweetObserver.setUp()
    }

    @ExperimentalCoroutinesApi
    fun resume() {
        tweetObserver.close()
        tweetObserver.observe()
        rssObserver.close()
        rssObserver.observe()
    }

    fun pause() {
        tweetObserver.close()
        rssObserver.close()
    }
}

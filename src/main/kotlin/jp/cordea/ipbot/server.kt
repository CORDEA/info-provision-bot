package jp.cordea.ipbot

import io.ktor.application.*
import jp.cordea.ipbot.rss.observeRss
import jp.cordea.ipbot.twitter.observeTweets
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
fun Application.main() {
    observeTweets()
    observeRss()
}

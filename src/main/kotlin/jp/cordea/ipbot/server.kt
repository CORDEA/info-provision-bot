package jp.cordea.ipbot

import io.ktor.application.*
import jp.cordea.ipbot.twitter.observeTweets

fun Application.main() {
    observeTweets()
}

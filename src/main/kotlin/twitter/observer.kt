package twitter

import client.StreamRuleRequest
import client.TwitterClient
import io.ktor.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun Application.observeTweets() {
    val token = environment.config.property("twitter.token").getString()
    val rules = environment.config.property("twitter.rules").getList()
    val client = TwitterClient(token)

    client
        .postStreamRules(rules.map { StreamRuleRequest(it) })
        .flowOn(Dispatchers.IO)
        .launchIn(this)

    client.getTweets(maxAttempts = 10)
        .flowOn(Dispatchers.IO)
        .onEach {
            // TODO
        }
        .launchIn(this)
}

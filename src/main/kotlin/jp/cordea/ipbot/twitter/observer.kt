package jp.cordea.ipbot.twitter

import io.ktor.application.*
import jp.cordea.ipbot.PostBroadcastMessageUseCase
import jp.cordea.ipbot.line.client.TextMessage
import jp.cordea.ipbot.twitter.client.StreamRuleRequest
import jp.cordea.ipbot.twitter.client.TwitterClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
fun Application.observeTweets(postBroadcastMessageUseCase: PostBroadcastMessageUseCase) {
    val token = environment.config.property("twitter.token").getString()
    val rules = environment.config.property("twitter.rules").getList()
    val client = TwitterClient(token)

    client
        .postStreamRules(rules.map { StreamRuleRequest(it) })
        .flowOn(Dispatchers.IO)
        .launchIn(this)

    client.getTweets(maxAttempts = 10)
        .map { TextMessage(it.data.text) }
        .flatMapLatest { postBroadcastMessageUseCase.execute(listOf(it)) }
        .flowOn(Dispatchers.IO)
        .launchIn(this)
}

package jp.cordea.ipbot.twitter.client

import kotlinx.coroutines.flow.flow

class StreamRuleRepository(
    private val client: TwitterClient
) {
    fun update(rules: List<String>) = flow {
        emit(client.postStreamRules(rules.map { StreamRuleRequest(it) }))
    }
}

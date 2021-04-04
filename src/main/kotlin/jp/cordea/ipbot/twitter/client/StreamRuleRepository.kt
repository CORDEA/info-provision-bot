package jp.cordea.ipbot.twitter.client

import kotlinx.coroutines.flow.flatMapLatest

class StreamRuleRepository(
    private val clientProvider: TwitterClient.Provider
) {
    fun update(rules: List<String>) =
        clientProvider.provide()
            .flatMapLatest { it.postStreamRules(rules.map { StreamRuleRequest(it) }) }
}

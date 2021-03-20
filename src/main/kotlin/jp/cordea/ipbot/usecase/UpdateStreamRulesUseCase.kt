package jp.cordea.ipbot.usecase

import jp.cordea.ipbot.twitter.client.StreamRuleRepository

class UpdateStreamRulesUseCase(
    private val repository: StreamRuleRepository
) {
    fun execute(rules: List<String>) = repository.update(rules)
}

package jp.cordea.ipbot.usecase

import jp.cordea.ipbot.twitter.client.TweetRepository

class GetTweetsUseCase(
    private val repository: TweetRepository
) {
    fun execute() = repository.findAll()
}

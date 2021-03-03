import io.ktor.application.*
import twitter.observeTweets

fun Application.main() {
    observeTweets()
}

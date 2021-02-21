import io.ktor.server.cio.*
import io.ktor.server.engine.*

fun main(args: Array<String>) {
    embeddedServer(CIO, commandLineEnvironment(args)).start(wait = true)
}

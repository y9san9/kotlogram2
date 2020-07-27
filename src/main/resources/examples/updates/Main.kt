package examples.updates

import examples.client


fun main() {
    client.updates {
        message {
            println(it.message)
        }
        message(false, textPredicate = { it?.contains(Regex("(Hi|Hello)")) ?: false }) {

        }
    }
}

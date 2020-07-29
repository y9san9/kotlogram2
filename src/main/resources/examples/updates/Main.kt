package examples.updates


import examples.client

fun main() {
    client.updates {
        message {
            println(it.message)
        }
        all {
            println(it)
        }
    }
}

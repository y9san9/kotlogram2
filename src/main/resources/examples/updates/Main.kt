package examples.updates


import examples.client

fun main() {
    client.updates {
        message {

        }
        all {
            println(it)
        }
    }
}

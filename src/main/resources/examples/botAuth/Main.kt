package examples.botAuth

import com.y9san9.kotlogram.models.entity.User
import examples.auth.scanner
import examples.client


fun main() {
    if (!client.isAuthorized) {
        println("Enter bot token: ")
        client.botAuth(scanner.nextLine())
    } else if (!client.me.bot) error("You already signed in as user")

    val alex = client.getByUsername("y9san9") as User
    alex.sendMessage("Started")

    client.updates {
        message {
            println(it.message)
        }
    }
}

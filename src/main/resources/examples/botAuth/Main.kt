package examples.botAuth

import com.y9san9.kotlogram.models.entity.User
import examples.client


fun main() {
    val alex = client.getByUsername("y9san9") as User
    val crinny = client.getByUsername("crinny") as User

    client.updates {
        message {
            filter {
                it.from.id in listOf(alex.id, crinny.id)
            }
            println("${it.from.fullname}: ${it.message}")
        }
    }
}

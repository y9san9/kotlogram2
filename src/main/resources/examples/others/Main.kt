package examples.others

import com.y9san9.kotlogram.models.entity.Channel
import examples.client


fun main() {
    val channel = client.getByUsername("b0mb3r4at") as Channel
    println(channel.title)
}

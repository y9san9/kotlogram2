package examples.others

import com.y9san9.kotlogram.models.entity.Channel
import examples.client


fun main() {
    val channel = client.getByUsername("username")!!.entity as Channel
    client.join(channel)
}

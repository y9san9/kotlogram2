package com.y9san9.kotlogram.entity

import com.github.badoualy.telegram.api.utils.toInputPeer
import com.github.badoualy.telegram.tl.api.*
import com.y9san9.kotlogram.KotlogramClient


open class Entity (
    val id: Int
)

@Suppress("UNUSED_PARAMETER")
fun TLAbsChat.wrap(client: KotlogramClient) : Entity? {
    return Channel(this as? TLChannel ?: return Chat(this as? TLChat ?: return null))
}

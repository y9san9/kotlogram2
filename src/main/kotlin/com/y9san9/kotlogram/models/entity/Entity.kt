@file: Suppress("unused")

package com.y9san9.kotlogram.models.entity

import com.github.badoualy.telegram.tl.api.*
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.models.Peer
import com.y9san9.kotlogram.models.markup.ReplyMarkup

open class Entity (
        val client: KotlogramClient,
        val peer: Peer
) {
    val id = peer.id
    fun sendMessage(
            text: String = "",
            silent: Boolean = false,
            clearDraft: Boolean = true,
            replyTo: Int? = null,
            replyMarkup: ReplyMarkup? = null,
            entities: Array<TLAbsMessageEntity> = arrayOf()
    ) = client.sendMessage(peer.input, text, silent, clearDraft, replyTo, replyMarkup, entities)
}

fun TLAbsChat.wrap(client: KotlogramClient) : Entity? {
    return Channel(client, this as? TLChannel ?: return Chat(client, this as? TLChat ?: return null))
}
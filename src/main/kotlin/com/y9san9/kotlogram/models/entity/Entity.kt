package com.y9san9.kotlogram.models.entity

import com.github.badoualy.telegram.tl.api.*
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.models.Peer
import com.y9san9.kotlogram.models.markup.ReplyMarkup
import com.y9san9.kotlogram.models.media.Media

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
        media: List<Media> = listOf(),
        scheduledDate: Long? = null,
        entities: List<TLAbsMessageEntity> = listOf()
    ) = client.sendMessage(
        this, text, silent, clearDraft, replyTo, replyMarkup, media, scheduledDate, entities
    )

    fun editMessage(
        id: Int,
        text: String? = null,
        replyMarkup: ReplyMarkup? = null,
        entities: Array<TLAbsMessageEntity>
    ) = client.editMessage(this, id, text, replyMarkup, entities)
}

fun TLAbsChat.wrap(client: KotlogramClient) : Entity? {
    return Channel(client, this as? TLChannel ?: return Chat(client, this as? TLChat ?: return null))
}
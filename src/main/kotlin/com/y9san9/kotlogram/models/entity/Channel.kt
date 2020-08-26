@file: Suppress("SpellCheckingInspection", "unused")

package com.y9san9.kotlogram.models.entity

import com.github.badoualy.telegram.api.utils.toInputPeer
import com.github.badoualy.telegram.tl.api.TLAbsChatPhoto
import com.github.badoualy.telegram.tl.api.TLChannel
import com.github.badoualy.telegram.tl.api.TLPeerChannel
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.models.wrap

class Channel(
        client: KotlogramClient,
        val source: TLChannel
) : Entity(client, TLPeerChannel(source.id).wrap(client)){
    val creator = source.creator
    val kicked = source.kicked
    val left = source.left
    val editor = source.editor
    val moderator = source.moderator
    val broadcast = source.broadcast
    val verified = source.verified
    val megagroup = source.megagroup
    val restricted = source.restricted
    val democracy = source.democracy
    val signatures = source.signatures
    val min = source.min
    val accessHash: Long = source.accessHash
    val title: String = source.title ?: ""
    val username: String? = source.username
    val photo: TLAbsChatPhoto? = source.photo
    val date = source.date
    val version = source.version
    val restrictionReason: String? = source.restrictionReason
    val input by lazy { source.toInputPeer()!! }
}
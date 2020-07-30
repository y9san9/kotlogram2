package com.y9san9.kotlogram.models.entity

import com.github.badoualy.telegram.api.utils.toInputPeer
import com.github.badoualy.telegram.tl.api.TLAbsChatPhoto
import com.github.badoualy.telegram.tl.api.TLAbsInputChannel
import com.github.badoualy.telegram.tl.api.TLChat
import com.github.badoualy.telegram.tl.api.TLPeerChat
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.models.wrap


class Chat(
        client: KotlogramClient,
        val source: TLChat
) : Entity(client, TLPeerChat(source.id).wrap(client)) {
    val creator = source.creator
    val kicked = source.kicked
    val left = source.left
    val adminsEnabled = source.adminsEnabled
    val admin = source.admin
    val deactivated = source.deactivated
    val title: String? = source.title
    val photo: TLAbsChatPhoto? = source.photo
    val participantsCount = source.participantsCount
    val date = source.date
    val version = source.version
    val migratedTo: TLAbsInputChannel? = source.migratedTo
    val input by lazy { source.toInputPeer()!! }
}

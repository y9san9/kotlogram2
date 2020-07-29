package com.y9san9.kotlogram.entity

import com.github.badoualy.telegram.api.utils.toInputPeer
import com.github.badoualy.telegram.tl.api.TLAbsChatPhoto
import com.github.badoualy.telegram.tl.api.TLAbsInputChannel
import com.github.badoualy.telegram.tl.api.TLChat


class Chat(
    val source: TLChat
) : Entity(source.id) {
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

package com.y9san9.kotlogram.entity

import com.github.badoualy.telegram.api.utils.toInputPeer
import com.github.badoualy.telegram.tl.api.*
import com.y9san9.kotlogram.KotlogramClient


sealed class Entity (
    val id: Int
)

class User(
    val source: TLUser
) : Entity(source.id) {
    val self = source.self
    val contact = source.contact
    val mutualContact = source.mutualContact
    val deleted = source.deleted
    val bot = source.bot
    val botChatHistory = source.botChatHistory
    val botNochats = source.botNochats
    val verified = source.verified
    val restricted = source.restricted
    val min = source.min
    val botInlineGeo = source.botInlineGeo
    val accessHash: Long = source.accessHash
    val firstName = source.firstName ?: ""
    val lastName: String? = source.lastName
    val username: String? = source.username
    val phone: String? = source.phone
    val photo: TLAbsUserProfilePhoto? = source.photo
    val status: TLAbsUserStatus? = source.status
    val botInfoVersion: Int? = source.botInfoVersion
    val restrictionReason: String? = source.restrictionReason
    val botInlinePlaceholder: String? = source.botInlinePlaceholder
    val langCode: String? = source.langCode
    val input = source.toInputPeer()
}
@Suppress("UNUSED_PARAMETER")
fun TLAbsUser.wrap(client: KotlogramClient) = User(asUser)

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

class Channel(
    val source: TLChannel
) : Entity(source.id){
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

@Suppress("UNUSED_PARAMETER")
fun TLAbsChat.wrap(client: KotlogramClient) : Entity? {
    return Channel(this as? TLChannel ?: return Chat(this as? TLChat ?: return null))
}

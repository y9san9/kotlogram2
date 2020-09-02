package com.y9san9.kotlogram.models.entity

import com.github.badoualy.telegram.api.utils.toInputPeer
import com.github.badoualy.telegram.tl.api.*
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.models.wrap

class User(
        client: KotlogramClient,
        val source: TLUser
) : Entity(client, TLPeerUser(source.id).wrap(client)) {
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
    val fullname = "$firstName${if(lastName == null) "" else " $lastName"}"
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

fun TLAbsUser.wrap(client: KotlogramClient) = User(client, asUser)
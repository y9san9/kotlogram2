package com.y9san9.kotlogram.models

import com.github.badoualy.telegram.api.utils.id
import com.github.badoualy.telegram.api.utils.toInputPeer
import com.github.badoualy.telegram.tl.api.*
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.models.entity.Channel
import com.y9san9.kotlogram.models.entity.Chat
import com.y9san9.kotlogram.models.entity.User


class Peer(client: KotlogramClient, source: TLAbsPeer){
    @Suppress("MemberVisibilityCanBePrivate")
    val id = source.id!!

    val isUser = source is TLPeerUser
    val isChat = source is TLPeerChat
    val isChannel = source is TLPeerChannel
    val isForbidden = source is TLChatForbidden || source is TLChannelForbidden

    @Suppress("MemberVisibilityCanBePrivate")
    val entity by lazy {
        when(source){
            is TLPeerUser -> client.getUser(id)
            is TLPeerChannel -> client.getChannel(id)
            is TLPeerChat -> client.getChat(id)
            else -> throw UnsupportedOperationException()
        }!!
    }

    val input by lazy {
        when(val ownerConst = entity){
            is User -> ownerConst.source.toInputPeer()
            is Channel -> ownerConst.source.toInputPeer()
            is Chat -> ownerConst.source.toInputPeer()
            else -> throw UnsupportedOperationException()
        }!!
    }
}
fun TLAbsPeer.wrap(client: KotlogramClient) = Peer(client, this)

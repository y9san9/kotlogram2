package com.y9san9.kotlogram.models

import com.github.badoualy.telegram.api.utils.id
import com.github.badoualy.telegram.api.utils.toInputPeer
import com.github.badoualy.telegram.tl.api.*
import com.y9san9.kotlogram.KotlogramClient
import examples.client


class Peer(client: KotlogramClient, source: TLAbsPeer){
    val id = source.id!!

    val isUser = source is TLPeerUser
    val isChat = source is TLPeerChat
    val isChannel = source is TLPeerChannel

    val input by lazy {
        when(source){
            is TLPeerUser -> client.getUser(id)?.source?.toInputPeer()
            is TLPeerChannel -> client.getChannel(id)?.source?.toInputPeer()
            is TLPeerChat -> client.getChat(id)?.source?.toInputPeer()
            else -> throw UnsupportedOperationException()
        }
    }
}
fun TLAbsPeer.wrap(client: KotlogramClient) = Peer(client, this)

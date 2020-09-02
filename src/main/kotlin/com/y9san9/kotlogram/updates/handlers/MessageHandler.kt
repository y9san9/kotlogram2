package com.y9san9.kotlogram.updates.handlers

import com.github.badoualy.telegram.tl.api.TLAbsUpdate
import com.github.badoualy.telegram.tl.api.TLUpdateNewChannelMessage
import com.github.badoualy.telegram.tl.api.TLUpdateNewMessage
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.models.Message
import com.y9san9.kotlogram.models.wrap


class MessageHandler (
    private val client: KotlogramClient,
    handler: EventHandler<Message>
) : Handler<Message>(handler) {
    @Suppress("UNCHECKED_CAST")
    override fun mapUpdate(update: TLAbsUpdate) = when(update){
        is TLUpdateNewMessage -> update.message
        is TLUpdateNewChannelMessage -> update.message
        else -> throw UnsupportedOperationException()
    }.wrap(client)
}

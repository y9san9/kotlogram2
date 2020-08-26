package com.y9san9.kotlogram.models

import com.github.badoualy.telegram.api.utils.*
import com.github.badoualy.telegram.tl.api.*
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.models.markup.ReplyMarkup
import com.y9san9.kotlogram.models.markup.wrap

fun TLAbsMessage.wrap(client: KotlogramClient) : Message = when(this){
    is TLMessage -> Message(client, this, null)
    is TLMessageService -> Message(client, TLMessage(
        out,
        mentioned,
        mediaUnread,
        silent,
        post,
        id,
        fromId,
        toId,
        null,
        null,
        replyToMsgId,
        date,
        null,
        null,
        null,
        null,
        null,
        null
    ), action)
    is TLMessageEmpty -> Message(client, TLMessage(
        false,
        false,
        false,
        false,
        false,
        id,
        fromId,
        toId,
        null,
        null,
        replyToMsgId,
        date,
        null,
        null,
        null,
        null,
        null,
        null
    ), null)
    else -> throw UnsupportedOperationException()
}

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter")
class Message(
    val client: KotlogramClient,
    val source: TLMessage,
    val action: TLAbsMessageAction?
){
    val id = source.id
    val isService = action == null
    val out = source.out
    val mentioned = source.mentioned
    val mediaUnread = source.mediaUnread
    val silent = source.silent
    val post = source.post
    val from by lazy {
        client.getUser(source.fromId)
            ?: throw UnsupportedOperationException("Cannot find owner by id ${source.fromId}")
    }
    val to = source.toId.wrap(client)
    val fwdFrom: TLMessageFwdHeader? = source.fwdFrom
    val viaBot by lazy {
        client.getUser(source.viaBotId)
            ?: throw UnsupportedOperationException("Cannot find via bot by id ${source.viaBotId}")
    }
    val isReply = source.isReply
    val replyToMsgId: Int? = source.replyToMsgId
    val reply by lazy { client.getMessage(source.replyToMsgId ?: return@lazy null) }
    val date = source.date
    val message: String? = source.message
    val media: TLAbsMessageMedia? = source.media
    val replyMarkup = source.replyMarkup?.wrap(client, this)
    val entities: List<TLAbsMessageEntity>? = source.entities
    val views: Int? = source.views
    val editDate: Int? = source.editDate

    fun reply(
        text: String = "",
        silent: Boolean = false,
        clearDraft: Boolean = true,
        replyMarkup: ReplyMarkup? = null,
        entities: Array<TLAbsMessageEntity> = arrayOf()
    ) = client.sendMessage(
        if(to.isUser && !out) from.source.toInputPeer() else to.input,
        text, silent, clearDraft, id, replyMarkup, entities
    )

    fun edit(
        text: String? = null,
        replyMarkup: ReplyMarkup? = null,
        entities: Array<TLAbsMessageEntity>
    ) = client.editMessage(to.input, id, text, replyMarkup, entities)

    fun delete(deleteForAll: Boolean = true) = client.deleteMessage(deleteForAll, id)

}
package com.y9san9.kotlogram.updates

import com.github.badoualy.telegram.api.TelegramClient
import com.github.badoualy.telegram.api.UpdateCallback
import com.github.badoualy.telegram.api.utils.id
import com.github.badoualy.telegram.api.utils.is1v1
import com.github.badoualy.telegram.api.utils.isChannel
import com.github.badoualy.telegram.tl.api.*
import com.github.badoualy.telegram.tl.api.updates.TLDifference
import com.github.badoualy.telegram.tl.api.updates.TLDifferenceSlice
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.entity.wrap
import com.y9san9.kotlogram.models.Message
import com.y9san9.kotlogram.models.wrap
import com.y9san9.kotlogram.utils.assert


class UpdatesHandler(private val client: KotlogramClient) : UpdateCallback {
    private var realHandler = UpdateDSL(client)
    fun handler(handler: UpdateDSL.() -> Unit) { realHandler = UpdateDSL(client).apply(handler) }

    override fun onShortChatMessage(client: TelegramClient, message: TLUpdateShortChatMessage)
            = handleShortMessageUpdate(client, message.pts, message.ptsCount, message.date)
    override fun onShortMessage(client: TelegramClient, message: TLUpdateShortMessage)
            = handleShortMessageUpdate(client, message.pts, message.ptsCount, message.date)
    override fun onShortSentMessage(client: TelegramClient, message: TLUpdateShortSentMessage)
            = handleShortMessageUpdate(client, message.pts, message.ptsCount, message.date)

    private fun handleShortMessageUpdate(client: TelegramClient, pts: Int, ptsCount: Int, date: Int){
        when(
            val difference = client.updatesGetDifference(pts - ptsCount, null, date, 0)
        ){
            is TLDifference -> {
                this.client.cachedEntities.addAll(difference.users.map { it.wrap(this.client) })
                this.client.cachedEntities.addAll(difference.chats.mapNotNull { it.wrap(this.client) })
                realHandler(
                    *difference.newMessages.map { TLUpdateNewMessage(it, pts, ptsCount) }.toTypedArray()
                )
            }
        }
    }

    override fun onUpdateShort(client: TelegramClient, update: TLUpdateShort) {
        realHandler(update.update)
    }

    override fun onUpdateTooLong(client: TelegramClient) {}

    override fun onUpdates(client: TelegramClient, updates: TLUpdates) {
        this.client.cachedEntities.addAll(updates.users.map { it.wrap(this.client) })
        this.client.cachedEntities.addAll(updates.chats.mapNotNull { it.wrap(this.client) })
        realHandler(*updates.updates.toTypedArray())
    }

    override fun onUpdatesCombined(client: TelegramClient, updates: TLUpdatesCombined) {
        this.client.cachedEntities.addAll(updates.users.map { it.wrap(this.client) })
        this.client.cachedEntities.addAll(updates.chats.mapNotNull { it.wrap(this.client) })
        realHandler(*updates.updates.toTypedArray())
    }

    class UpdateDSL(private val client: KotlogramClient) {
        private val handlers = mutableListOf<HandlerDSL<*>>()
        abstract class HandlerDSL<T> {
            abstract val handler: (T) -> Unit
            /**
             * @throws Throwable or
             * @return null if cannot intercept update
             */
            abstract fun mapUpdate(update: TLAbsUpdate) : T
        }

        class MessageHandler(
            private val client: KotlogramClient,
            override val handler: (Message) -> Unit,
            private val out: Boolean?,
            private val toChannel: Boolean?,
            private val toChannelId: Int?,
            private val toUserId: Int?,
            private val fromUserId: Int?,
            private val textPredicate: ((String?) -> Boolean)? = null
        ) : HandlerDSL<Message>() {
            @Suppress("UNCHECKED_CAST")
            override fun mapUpdate(update: TLAbsUpdate) = when(update){
                is TLUpdateNewMessage -> update.message
                is TLUpdateNewChannelMessage -> update.message
                else -> throw UnsupportedOperationException()
            }.wrap(client).also {
                out?.apply { out assert it.out }
                toChannel?.apply { toChannel assert it.to.isChannel }
                toChannelId?.apply {
                    it.to.isChannel assert true
                    it.to.id assert toChannelId
                }
                toUserId?.apply {
                    it.to.isUser assert true
                    it.to.id assert toUserId
                }
                fromUserId?.apply {
                    it.from.id assert fromUserId
                }
                textPredicate?.apply {
                    textPredicate.invoke(it.message) assert true
                }
            }
        }
        fun message(
            out: Boolean? = null,
            toChannel: Boolean? = null,
            toChannelId: Int? = null,
            toUserId: Int? = null,
            fromUserId: Int? = null,
            textPredicate: ((String?) -> Boolean)? = null,
            handler: (Message) -> Unit
        ) = handlers.add(MessageHandler(client, handler, out, toChannel, toChannelId, toUserId, fromUserId, textPredicate))

        class AllHandler(override val handler: (TLAbsUpdate) -> Unit) : HandlerDSL<TLAbsUpdate>(){
            override fun mapUpdate(update: TLAbsUpdate) = update
        }
        fun all(handler: (TLAbsUpdate) -> Unit) = handlers.add(AllHandler(handler))

        operator fun invoke(vararg update: TLAbsUpdate) = update.forEach {
            for(handler in handlers)
                handleUpdate(handler, it)
        }
        private fun <T> handleUpdate(handler: HandlerDSL<T>, update: TLAbsUpdate){
            try {
                handler.handler(handler.mapUpdate(update))
            } catch (_: Throwable){}
        }
    }
}

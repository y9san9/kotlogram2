package com.y9san9.kotlogram.updates

import com.github.badoualy.telegram.api.TelegramClient
import com.github.badoualy.telegram.api.UpdateCallback
import com.github.badoualy.telegram.tl.api.*
import com.github.badoualy.telegram.tl.api.updates.TLDifference
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.models.entity.wrap
import com.y9san9.kotlogram.models.Message
import com.y9san9.kotlogram.models.wrap

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
                this.client.addEntities(difference.users, difference.chats)
                realHandler(
                    *(difference.newMessages.map { TLUpdateNewMessage(it, pts, ptsCount) }
                            + difference.otherUpdates.toList()).toTypedArray()
                )
            }
        }
    }

    override fun onUpdateShort(client: TelegramClient, update: TLUpdateShort) {
        realHandler(update.update)
    }

    override fun onUpdateTooLong(client: TelegramClient) = TODO()

    override fun onUpdates(client: TelegramClient, updates: TLUpdates) {
        this.client.addEntities(updates.users, updates.chats)
        realHandler(*updates.updates.toTypedArray())
    }

    override fun onUpdatesCombined(client: TelegramClient, updates: TLUpdatesCombined) {
        this.client.addEntities(updates.users, updates.chats)
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
            private val messagePredicate: (Message) -> Unit
        ) : HandlerDSL<Message>() {
            @Suppress("UNCHECKED_CAST")
            override fun mapUpdate(update: TLAbsUpdate) = when(update){
                is TLUpdateNewMessage -> update.message
                is TLUpdateNewChannelMessage -> update.message
                else -> throw UnsupportedOperationException()
            }.wrap(client).also {
                messagePredicate(it)
            }
        }

        /**
         * @param messagePredicate - throw error or nothing
         */
        fun message(
            messagePredicate: (Message) -> Unit = { },
            handler: (Message) -> Unit
        ) = handlers.add(MessageHandler(client, handler, messagePredicate))

        class AllHandler(override val handler: (TLAbsUpdate) -> Unit) : HandlerDSL<TLAbsUpdate>(){
            override fun mapUpdate(update: TLAbsUpdate) = update
        }
        fun all(handler: (TLAbsUpdate) -> Unit) = handlers.add(AllHandler(handler))

        operator fun invoke(vararg update: TLAbsUpdate) = update.forEach {
            for(handler in handlers)
                handleUpdate(handler, it)
        }
        private fun <T> handleUpdate(handler: HandlerDSL<T>, update: TLAbsUpdate) = try {
            handler.handler(handler.mapUpdate(update))
        } catch (_: Throwable){}
    }
}
package com.y9san9.kotlogram.updates

import com.github.badoualy.telegram.api.TelegramClient
import com.github.badoualy.telegram.api.UpdateCallback
import com.github.badoualy.telegram.tl.api.*
import com.github.badoualy.telegram.tl.api.updates.TLDifference
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.models.Message
import com.y9san9.kotlogram.models.wrap


typealias EventHandler<T> = EventDSL.(T) -> Unit

class EventDSL {
    fun filter(filter: () -> Boolean) {
        if(!filter())
            throw EventFiltered()
    }
    class EventFiltered : IllegalStateException()
}


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
        private val handlers = mutableListOf<Handler<*>>()
        abstract class Handler<T>(val handler: EventHandler<T>) {
            /**
             * @throws Throwable or
             * @return null if cannot intercept update
             */
            abstract fun mapUpdate(update: TLAbsUpdate) : T
        }

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

        fun message(
            handler: EventHandler<Message>
        ) = handlers.add(MessageHandler(client, handler))

        class AllHandler(
            handler: EventHandler<TLAbsUpdate>
        ) : Handler<TLAbsUpdate>(handler){
            override fun mapUpdate(update: TLAbsUpdate) = update
        }
        fun all(
            handler: EventHandler<TLAbsUpdate>
        ) = handlers.add(AllHandler(handler))

        operator fun invoke(vararg update: TLAbsUpdate) = update.forEach {
            for(handler in handlers)
                handleUpdate(handler, it)
        }
        private fun <T> handleUpdate(handler: Handler<T>, update: TLAbsUpdate) = try {
            try {
                handler.handler(EventDSL(), handler.mapUpdate(update))
            } catch (_: EventDSL.EventFiltered) { }
        } catch (_: Throwable){}
    }
}
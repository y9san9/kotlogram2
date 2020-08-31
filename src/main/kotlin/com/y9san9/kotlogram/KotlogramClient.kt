package com.y9san9.kotlogram

import com.github.badoualy.telegram.api.Kotlogram
import com.github.badoualy.telegram.tl.api.*
import com.github.badoualy.telegram.tl.core.TLVector
import com.github.badoualy.telegram.tl.exception.RpcErrorException
import com.y9san9.kotlogram.dsl.auth.AuthDSL
import com.y9san9.kotlogram.models.TelegramApp
import com.y9san9.kotlogram.models.entity.*
import com.y9san9.kotlogram.models.extentions.input
import com.y9san9.kotlogram.models.markup.ReplyMarkup
import com.y9san9.kotlogram.models.wrap
import com.y9san9.kotlogram.storage.ApiStorage
import com.y9san9.kotlogram.updates.UpdatesHandler
import com.y9san9.kotlogram.utils.intVectorOf
import com.y9san9.kotlogram.utils.vectorOf
import kotlin.random.Random


class KotlogramClient(app: TelegramApp, sessionName: String = "") {
    private val cachedEntities = mutableListOf<Entity>()
    internal fun addEntities(users: TLVector<TLAbsUser>? = null, chats: TLVector<TLAbsChat>? = null) {
        users?.let {
            cachedEntities.addAll(users.map { it.wrap(this) })
        }
        chats?.let {
            cachedEntities.addAll(chats.mapNotNull { it.wrap(this) })
        }
    }

    private val updateCallback = UpdatesHandler(this)
    fun updates(handler: UpdatesHandler.UpdateDSL.() -> Unit) = updateCallback.handler(handler)

    @Suppress("MemberVisibilityCanBePrivate")
    val client = Kotlogram.getDefaultClient(
            app.toKotlogramApp(), ApiStorage(sessionName), updateCallback = updateCallback
    )

    fun getChannel(id: Int): Channel? = cachedEntities.firstOrNull {
        it is Channel && it.peer.id == id
    } as Channel? ?: (getChannelPrivate(id) as? TLChannel)?.let {
        (it.wrap(this) as Channel).apply { cachedEntities.add(this) }
    }

    fun getByUsername(username: String) = try {
        client.contactsResolveUsername(username).also {
            addEntities(it.users, it.chats)
        }.peer.wrap(this).entity
    } catch (_: RpcErrorException) {
        null
    }

    fun getChat(id: Int): Chat? = cachedEntities.firstOrNull {
        it is Chat && it.peer.id == id
    } as Chat? ?: (getChannelPrivate(id) as? TLChat)?.let {
        (it.wrap(this) as Chat).apply { cachedEntities.add(this) }
    }

    fun getUser(id: Int): User? = cachedEntities.firstOrNull {
        it is User && it.peer.id == id
    } as User? ?: getUserPrivate(id)?.let {
        it.wrap(this).apply { cachedEntities.add(this) }
    }

    private fun getChannelPrivate(id: Int)
            = client.channelsGetChannels(vectorOf(TLInputChannel(id, 0))).chats[0]
    private fun getUserPrivate(id: Int)
            = client.usersGetUsers(vectorOf(TLInputUser(id, 0))).firstOrNull()

    fun getMessage(id: Int) = getMessages(id)[0]
    @Suppress("MemberVisibilityCanBePrivate")
    fun getMessages(vararg ids: Int) = client.messagesGetMessages(intVectorOf(*ids)).messages.map {
        it.wrap(this)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun auth(
            phone: String,
            allowFlashcall: Boolean = false,
            currentNumber: Boolean = true,
            handler: AuthDSL.() -> Unit
    ): Unit = try {
        val sentCode = client.authSendCode(allowFlashcall, phone, currentNumber)
        val dsl = AuthDSL(sentCode.wrap(this),
            codeReceiver = { code ->
                try {
                    val auth = client.authSignIn(phone, sentCode.phoneCodeHash, code)
                    signedHandler(auth.user.wrap(this@KotlogramClient))
                    true
                } catch (e: RpcErrorException) {
                    if(e.code == 401){
                        passwordHandler()
                        true
                    } else false
                }
            },
            passwordReceiver = { password ->
                try {
                    val auth = client.authCheckPassword(password)
                    signedHandler(auth.user.wrap(this@KotlogramClient))
                    true
                } catch (_: RpcErrorException) {
                    false
                }
            }
        ).apply(handler)
        dsl.codeHandler()
    } catch (e: RpcErrorException) {
        if (e.code == 500) {
            auth(phone, allowFlashcall, currentNumber, handler)
        } else throw e
    }

    val isAuthorized get() = try {
        client.updatesGetState()
        true
    } catch (_: RpcErrorException){ false }


    /* MESSAGE METHODS */

    fun sendMessage(
            to: TLAbsInputPeer,
            text: String? = null,
            silent: Boolean = false,
            clearDraft: Boolean = true,
            replyTo: Int? = null,
            replyMarkup: ReplyMarkup? = null,
            entities: Array<TLAbsMessageEntity> = arrayOf()
    ) = client.messagesSendMessage(
        true, silent, false, clearDraft, to, replyTo, text,
            Random.nextLong(), replyMarkup?.unwrap(), vectorOf(*entities)
    ).let {  }

    fun editMessage(
        to: TLAbsInputPeer,
        id: Int,
        text: String? = null,
        replyMarkup: ReplyMarkup? = null,
        entities: Array<TLAbsMessageEntity>
    ) = client.messagesEditMessage(
            true, to, id, text, replyMarkup?.unwrap(), vectorOf(*entities)
    ).let { }

    fun deleteMessage(deleteForAll: Boolean = true, vararg ids: Int): Unit = client.messagesDeleteMessages(
        deleteForAll, intVectorOf(*ids)
    ).let { }


    /* CHANNEL METHODS */

    fun join(channel: Channel) = join(channel.source.id, channel.source.accessHash)

    @Suppress("MemberVisibilityCanBePrivate")
    fun join(channelId: Int, accessHash: Long = 0) = join(TLInputChannel(channelId, accessHash))

    fun join(inviteLink: String) = joinByInviteHash(inviteLink.replaceFirst("https://t.me/joinchat/", ""))

    @Suppress("MemberVisibilityCanBePrivate")
    fun joinByInviteHash(hash: String) = client.messagesImportChatInvite(hash).let { }

    private fun join(channel: TLInputChannel) = client.channelsJoinChannel(channel).let { }

    fun kick(channel: Channel, user: User, kicked: Boolean = true) {
        client.channelsKickFromChannel(channel.source.input, user.source.input, kicked)
    }
}

package com.y9san9.kotlogram

import com.github.badoualy.telegram.api.Kotlogram
import com.github.badoualy.telegram.tl.api.*
import com.github.badoualy.telegram.tl.exception.RpcErrorException
import com.y9san9.kotlogram.models.TelegramApp
import com.y9san9.kotlogram.models.entity.*
import com.y9san9.kotlogram.models.markup.ReplyMarkup
import com.y9san9.kotlogram.models.wrap
import com.y9san9.kotlogram.storage.ApiStorage
import com.y9san9.kotlogram.updates.UpdatesHandler
import com.y9san9.kotlogram.utils.intVectorOf
import com.y9san9.kotlogram.utils.vectorOf
import kotlin.random.Random

class KotlogramClient(app: TelegramApp, sessionName: String = "") {
    internal val cachedEntities = mutableListOf<Entity>()

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
            cachedEntities.addAll(
                    (it.chats?.mapNotNull {
                        entity -> entity.wrap(this)
                    } ?: listOf())
                            + (it.users?.mapNotNull {
                        user -> user.wrap(this)
                    } ?: listOf())
            )
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
        passwordHandler: () -> String = {
            throw UnsupportedOperationException("Account has two factor auth, but handler was not set")
        },
        codeHandler: () -> String
    ): Unit = try {
        val sentCode = client.authSendCode(false, phone, true)
        val confirmationCode = codeHandler()
        val auth = try {
            client.authSignIn(phone, sentCode.phoneCodeHash, confirmationCode)
        } catch (e: RpcErrorException){
            if(e.code == 401){
                client.authCheckPassword(passwordHandler())
            } else throw e
        }
        println("Signed in as ${auth.user.asUser.firstName}")
    } catch (e: RpcErrorException) {
        if (e.code == 500) {
            auth(phone, passwordHandler, codeHandler)
        } else throw e
    }

    val isAuthorized get() = try {
        client.updatesGetState()
        true
    } catch (_: RpcErrorException){ false }

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

    fun join(channel: Channel) = join(channel.source.id, channel.source.accessHash)

    @Suppress("MemberVisibilityCanBePrivate")
    fun join(channelId: Int, accessHash: Long = 0) = join(TLInputChannel(channelId, accessHash))

    fun join(inviteLink: String) = joinByInviteHash(inviteLink.replaceFirst("https://t.me/joinchat/", ""))

    @Suppress("MemberVisibilityCanBePrivate")
    fun joinByInviteHash(hash: String) = client.messagesImportChatInvite(hash).let { }

    private fun join(channel: TLInputChannel) = client.channelsJoinChannel(channel).let { }
}
package com.y9san9.kotlogram

import com.github.badoualy.telegram.api.Kotlogram
import com.github.badoualy.telegram.api.utils.InputFileLocation
import com.github.badoualy.telegram.tl.api.*
import com.github.badoualy.telegram.tl.core.TLBool
import com.github.badoualy.telegram.tl.core.TLBytes
import com.github.badoualy.telegram.tl.core.TLVector
import com.github.badoualy.telegram.tl.exception.RpcErrorException
import com.y9san9.kotlogram.dsl.auth.AuthDSL
import com.y9san9.kotlogram.models.SentCode
import com.y9san9.kotlogram.models.TelegramApp
import com.y9san9.kotlogram.models.entity.*
import com.y9san9.kotlogram.utils.input
import com.y9san9.kotlogram.models.markup.ReplyMarkup
import com.y9san9.kotlogram.models.media.*
import com.y9san9.kotlogram.models.wrap
import com.y9san9.kotlogram.storage.ApiStorage
import com.y9san9.kotlogram.updates.UpdatesHandler
import com.y9san9.kotlogram.utils.intVectorOf
import com.y9san9.kotlogram.utils.vectorOf
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.random.Random


private val scanner = Scanner(System.`in`)

class KotlogramClient(private val app: TelegramApp, sessionName: String = "") {
    private val updateCallback = UpdatesHandler(this)
    fun updates(handler: UpdatesHandler.UpdateDSL.() -> Unit) = updateCallback.handler(handler)

    private val storage = ApiStorage(sessionName)
    @Suppress("MemberVisibilityCanBePrivate")
    val client = Kotlogram.getDefaultClient(
            app.toKotlogramApp(), storage, updateCallback = updateCallback
    )

    val me by lazy {
        client.usersGetUsers(vectorOf(TLInputUserSelf()))[0].wrap(this)
    }

    private val cachedEntities = mutableListOf<Entity>()
    internal fun addEntities(users: TLVector<TLAbsUser>? = null, chats: TLVector<TLAbsChat>? = null) {
        users?.let {
            cachedEntities.addAll(users.map { it.wrap(this) })
        }
        chats?.let {
            cachedEntities.addAll(chats.mapNotNull { it.wrap(this) })
        }
    }

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

    fun botAuth(token: String)
            = client.authImportBotAuthorization(0, app.apiId, app.apiHash, token).user.wrap(this)

    @Suppress("MemberVisibilityCanBePrivate")
    fun auth(
            phone: String,
            allowFlashcall: Boolean = false,
            currentNumber: Boolean = true,
            handler: AuthDSL.() -> Unit = {
                code {
                    do {
                        print("Enter code from telegram: ")
                        val code = scanner.nextLine()
                    } while (!check(code))
                }
                password {
                    do {
                        print("Enter account password: ")
                        val password = scanner.nextLine()
                    } while (!check(password))
                }
                signed {
                    println("Signed in as ${it.firstName}")
                }
            }
    ): Unit = try {
        val sentCode = client.authSendCode(allowFlashcall, phone, currentNumber)
        val dsl = AuthDSL(sentCode.wrap(phone, this),
            codeReceiver = { code ->
                try {
                    val auth = client.authSignIn(phone, sentCode.phoneCodeHash, code)
                    signedHandler(auth.user.wrap(this@KotlogramClient))
                    true
                } catch (e: RpcErrorException) {
                    println(e)
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

    fun resendAuthCode(code: SentCode)
            = client.authResendCode(code.phone, code.hash).wrap(code.phone, this)
    fun cancelAuthCode(code: SentCode)
            = client.authCancelCode(code.phone, code.hash) == TLBool.TRUE

    /* MESSAGE METHODS */
    fun sendMessage(
            to: Entity,
            text: String? = null,
            silent: Boolean = false,
            clearDraft: Boolean = true,
            replyTo: Int? = null,
            replyMarkup: ReplyMarkup? = null,
            media: List<Media> = listOf(),
            scheduledDate: Long? = null,
            entities: List<TLAbsMessageEntity> = listOf()
    ) = if(media.isEmpty()) {
        client.messagesSendMessage(
            true, silent, false,
            clearDraft, to.peer.input, replyTo, text,
            Random.nextLong(), replyMarkup?.unwrap(), vectorOf(*entities.toTypedArray()), scheduledDate
        ).let { }
    } else {

    }

    fun editMessage(
        to: Entity,
        id: Int,
        text: String? = null,
        replyMarkup: ReplyMarkup? = null,
        entities: Array<TLAbsMessageEntity>
    ) = client.messagesEditMessage(
            true, to.peer.input, id, text, replyMarkup?.unwrap(), vectorOf(*entities)
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

    /* FILE METHODS */
    fun download(fileLocation: FileLocation) = download(fileLocation.input, fileLocation.size)
    fun download(document: Document) = download(document.input, document.size)
    fun download(photo: Photo, size: PhotoSize) = download(
        InputFileLocation(
                TLInputPhotoFileLocation(photo.id, photo.accessHash, TLBytes(byteArrayOf()), size.source),
                size.fileLocation.input.dcId
        ), size.fileLocation.size
    )

    private fun download(location: InputFileLocation, size: Int) : ByteArray {
        val output = ByteArrayOutputStream()
        client.downloadSync(location, size, output)
        return output.toByteArray()
    }

    /**
     * Saves a part of a large file (over 10Mb in size) to be later passed to one of the methods.
     */
    fun uploadFile(file: File) = uploadFile(file.readBytes())
    fun uploadFile(bytes: ByteArray) = client.uploadSaveBigFilePart(
        Random.nextLong(), 1, 1, TLBytes(bytes)
    ) == TLBool.TRUE
}

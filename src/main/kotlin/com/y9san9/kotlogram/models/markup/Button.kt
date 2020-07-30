package com.y9san9.kotlogram.models.markup

import com.github.badoualy.telegram.tl.api.*
import com.github.badoualy.telegram.tl.core.TLBytes
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.models.Message
import javax.swing.plaf.ButtonUI


enum class ButtonType {
    Button, ButtonBuy, ButtonCallback, ButtonGame, ButtonRequestGeoLocation,
    ButtonRequestPhone, ButtonSwitchInline, ButtonUrl
}

@Suppress("MemberVisibilityCanBePrivate")
class Button private constructor(
        val message: Message? = null,
        val buttonType: ButtonType,
        val text: String? = null,
        val data: TLBytes? = null,
        val samePeer: Boolean = false,
        val query: String? = null,
        val url: String? = null
) {
    val client by lazy { message!!.client }

    companion object {
        fun button(text: String?, message: Message? = null)
                = Button(message, ButtonType.Button, text)
        fun buttonBuy(text: String?, message: Message? = null)
                = Button(message, ButtonType.ButtonBuy, text)
        fun buttonCallback(text: String?, data: TLBytes?, message: Message? = null)
                = Button(message, ButtonType.ButtonCallback, text, data)
        fun buttonGame(text: String?, message: Message? = null)
                = Button(message, ButtonType.ButtonGame, text)
        fun buttonRequestLocation(text: String?, message: Message? = null)
                = Button(message, ButtonType.ButtonRequestGeoLocation, text)
        fun buttonRequestPhone(text: String?, message: Message? = null)
                = Button(message, ButtonType.ButtonRequestPhone, text)
        fun buttonSwitchInline(samePeer: Boolean, text: String?, query: String?, message: Message? = null)
                = Button(message, ButtonType.ButtonSwitchInline, text, samePeer = samePeer, query = query)
        fun buttonUrl(text: String?, url: String?, message: Message? = null)
                = Button(message, ButtonType.ButtonUrl, text, url = url)
    }

    fun click(game: Boolean = false) = when(buttonType){
        ButtonType.Button -> client.sendMessage(message!!.to.input, text)
        ButtonType.ButtonCallback -> message!!.client.client.messagesGetBotCallbackAnswer(
                game, message.to.input, message.id, data
        ).let { }
        else -> throw UnsupportedOperationException()
    }

    fun unwrap() = when(buttonType){
        ButtonType.Button -> TLKeyboardButton(text)
        ButtonType.ButtonBuy -> TLKeyboardButtonBuy(text)
        ButtonType.ButtonCallback -> TLKeyboardButtonCallback(text, data)
        ButtonType.ButtonGame -> TLKeyboardButtonGame(text)
        ButtonType.ButtonRequestGeoLocation -> TLKeyboardButtonRequestGeoLocation(text)
        ButtonType.ButtonRequestPhone -> TLKeyboardButtonRequestPhone(text)
        ButtonType.ButtonSwitchInline -> TLKeyboardButtonSwitchInline(samePeer, text, query)
        ButtonType.ButtonUrl -> TLKeyboardButtonUrl(text, url)
    }
}

fun TLAbsKeyboardButton.wrap(message: Message? = null) = when(this){
    is TLKeyboardButton -> Button.button(text, message)
    is TLKeyboardButtonBuy -> Button.buttonBuy(text, message)
    is TLKeyboardButtonCallback -> Button.buttonCallback(text, data, message)
    is TLKeyboardButtonGame -> Button.buttonGame(text, message)
    is TLKeyboardButtonRequestGeoLocation -> Button.buttonRequestLocation(text, message)
    is TLKeyboardButtonRequestPhone -> Button.buttonRequestPhone(text, message)
    is TLKeyboardButtonSwitchInline -> Button.buttonSwitchInline(samePeer, text, query, message)
    is TLKeyboardButtonUrl -> Button.buttonUrl(text, url, message)
    else -> throw UnsupportedOperationException()
}

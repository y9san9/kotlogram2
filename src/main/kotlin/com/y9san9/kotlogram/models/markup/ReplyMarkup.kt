package com.y9san9.kotlogram.models.markup

import com.github.badoualy.telegram.tl.api.*
import com.github.badoualy.telegram.tl.core.TLVector
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.models.Message
import com.y9san9.kotlogram.utils.vectorOf


enum class Action {
    No, Hide, Reply
}

@Suppress("MemberVisibilityCanBePrivate")
class ReplyMarkup private constructor(
        val client: KotlogramClient?,
        val action: Action,
        val isInline: Boolean,
        val singleUse: Boolean,
        val selective: Boolean,
        val resize: Boolean,
        private val buttons: List<List<Button>>
){
    companion object {
        fun keyboard(
                client: KotlogramClient,
                buttons: List<List<Button>>,
                singleUse: Boolean = true,
                resize: Boolean = true,
                selective: Boolean = true
        ) = ReplyMarkup(client, Action.No, false, singleUse, selective, resize, buttons)

        fun inline(
                client: KotlogramClient,
                buttons: List<List<Button>>
        ) = ReplyMarkup(client, Action.No, isInline = true, singleUse = true, selective = true, resize = true,
                buttons = buttons)

        fun forceReply(singleUse: Boolean = true, selective: Boolean = true) = ReplyMarkup(
                null, Action.Reply, false, singleUse, selective, false, listOf()
        )

        fun hide(selective: Boolean = true) = ReplyMarkup(
                null,
                Action.Hide,
                isInline = false,
                singleUse = false,
                selective = selective,
                resize = false,
                buttons = listOf()
        )
    }

    operator fun get(row: Int, line: Int) = buttons[row][line]

    fun unwrap() = when(action){
        Action.Reply -> TLReplyKeyboardForceReply(singleUse, selective)
        Action.Hide -> TLReplyKeyboardHide(selective)
        Action.No -> if(isInline)
            TLReplyInlineMarkup(buttons.wrapWithVectors())
        else TLReplyKeyboardMarkup(resize, singleUse, selective, buttons.wrapWithVectors())
    }

    private fun List<List<Button>>.wrapWithVectors() = vectorOf(*map { TLKeyboardButtonRow(
            vectorOf(*it.map { button ->  button.unwrap() }.toTypedArray())
    ) }.toTypedArray())
}

fun TLAbsReplyMarkup.wrap(client: KotlogramClient, message: Message) = when(this){
    is TLReplyInlineMarkup -> ReplyMarkup.inline(client, rows.wrapWithList(message))
    is TLReplyKeyboardMarkup -> ReplyMarkup.keyboard(client, rows.wrapWithList(message), singleUse, resize, selective)
    is TLReplyKeyboardForceReply -> ReplyMarkup.forceReply(singleUse, selective)
    is TLReplyKeyboardHide -> ReplyMarkup.hide(selective)
    else -> throw UnsupportedOperationException()
}

private fun TLVector<TLKeyboardButtonRow>.wrapWithList(message: Message)
        = map { it.buttons.map { button -> button.wrap(message) } }

private fun List<List<TLAbsKeyboardButton>>.wrap(message: Message?)
        = map { it.map { button -> button.wrap(message) } }


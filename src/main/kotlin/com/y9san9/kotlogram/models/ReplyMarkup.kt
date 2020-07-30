package com.y9san9.kotlogram.models

import com.github.badoualy.telegram.tl.api.*
import com.github.badoualy.telegram.tl.core.TLVector
import com.y9san9.kotlogram.KotlogramClient
import com.y9san9.kotlogram.utils.vectorOf
import java.util.*


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
        private val buttons: List<List<TLAbsKeyboardButton>>
){
    companion object {
        fun keyboard(
                client: KotlogramClient,
                buttons: List<List<TLAbsKeyboardButton>>,
                singleUse: Boolean = true,
                resize: Boolean = true,
                selective: Boolean = true
        ) = ReplyMarkup(client, Action.No, false, singleUse, selective, resize, buttons)

        fun inline(
                client: KotlogramClient,
                buttons: List<List<TLAbsKeyboardButton>>
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

    private fun List<List<TLAbsKeyboardButton>>.wrapWithVectors() = vectorOf(*map { TLKeyboardButtonRow(
            vectorOf(*it.toTypedArray())
    ) }.toTypedArray())
}

fun TLAbsReplyMarkup.wrap(client: KotlogramClient) = when(this){
    is TLReplyInlineMarkup -> ReplyMarkup.inline(client, rows.wrapWithList())
    is TLReplyKeyboardMarkup -> ReplyMarkup.keyboard(client, rows.wrapWithList(), singleUse, resize, selective)
    is TLReplyKeyboardForceReply -> ReplyMarkup.forceReply(singleUse, selective)
    is TLReplyKeyboardHide -> ReplyMarkup.hide(selective)
    else -> throw UnsupportedOperationException()
}

private fun TLVector<TLKeyboardButtonRow>.wrapWithList() = map { it.buttons }

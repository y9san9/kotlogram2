package com.y9san9.kotlogram.models

import com.github.badoualy.telegram.tl.api.auth.*
import com.y9san9.kotlogram.KotlogramClient


enum class CodeType {
    Call, Flashcall, Sms
}

fun TLAbsCodeType.wrap() = when(this) {
    is TLCodeTypeCall -> CodeType.Call
    is TLCodeTypeFlashCall -> CodeType.Flashcall
    is TLCodeTypeSms -> CodeType.Sms
    else -> throw UnsupportedOperationException()
}

enum class SentCodeType {
    Message, Call, Flashcall, Sms;
}

fun TLAbsSentCodeType.wrap() = when(this) {
    is TLSentCodeTypeApp -> SentCodeType.Message
    is TLSentCodeTypeCall -> SentCodeType.Call
    is TLSentCodeTypeFlashCall -> SentCodeType.Flashcall
    is TLSentCodeTypeSms -> SentCodeType.Sms
    else -> throw UnsupportedOperationException()
}

class SentCode(
    val client: KotlogramClient,
    val phone: String,
    val hash: String,
    val timeout: Long,
    val type: SentCodeType,
    val nextType: CodeType
) {
    fun resend() = client.resendAuthCode(this)
    fun cancel() = client.cancelAuthCode(this)
}

fun TLSentCode.wrap(phone: String, client: KotlogramClient)
        = SentCode(client, phone, phoneCodeHash, timeout?.toLong() ?: 0, type.wrap(), nextType.wrap())

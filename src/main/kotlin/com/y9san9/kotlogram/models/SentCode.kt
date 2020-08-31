package com.y9san9.kotlogram.models

import com.github.badoualy.telegram.tl.api.auth.*
import com.y9san9.kotlogram.KotlogramClient


enum class CodeType {
    Message, Call, Flashcall, Sms;
}

fun TLAbsSentCodeType.wrap() = when(this) {
    is TLSentCodeTypeApp -> CodeType.Message
    is TLSentCodeTypeCall -> CodeType.Call
    is TLSentCodeTypeFlashCall -> CodeType.Flashcall
    is TLSentCodeTypeSms -> CodeType.Sms
    else -> throw UnsupportedOperationException()
}

class SentCode(
    val timeout: Long,
    val type: CodeType
)

fun TLSentCode.wrap(@Suppress("UNUSED_PARAMETER") client: KotlogramClient)
        = SentCode(timeout.toLong(), type.wrap())

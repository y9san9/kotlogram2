package com.y9san9.kotlogram.dsl.auth

import com.y9san9.kotlogram.models.SentCode
import com.y9san9.kotlogram.models.entity.User


class AuthDSL(
    private val sentCode: SentCode,
    private val codeReceiver: AuthDSL.(String) -> Boolean,
    private val passwordReceiver: AuthDSL.(String) -> Boolean
) {
    inner class CodeHandler(val sentCode: SentCode) {
        fun check(code: String) = codeReceiver(code)
    }
    internal var codeHandler: () -> Unit = { }
    fun code(handler: CodeHandler.() -> Unit) = CodeHandler(sentCode).also {
        codeHandler = {
            it.handler()
        }
    }

    inner class PasswordHandler {
        fun check(password: String) = passwordReceiver(password)
    }
    internal var passwordHandler: () -> Unit = {
        throw UnsupportedOperationException("Account has two factor auth, but handler was not set")
    }
    fun password(handler: PasswordHandler.() -> Unit) = PasswordHandler().also {
        passwordHandler = {
            it.handler()
        }
    }

    internal var signedHandler: (User) -> Unit = {}
    fun signed(handler: (User) -> Unit) {
        signedHandler = handler
    }
}
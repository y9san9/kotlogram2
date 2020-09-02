package com.y9san9.kotlogram.dsl.auth

import com.y9san9.kotlogram.models.SentCode
import com.y9san9.kotlogram.models.entity.User
import examples.auth.scanner


class AuthDSL(
    private val sentCode: SentCode,
    private val codeReceiver: AuthDSL.(String) -> Boolean,
    private val passwordReceiver: AuthDSL.(String) -> Boolean
) {
    inner class CodeHandler(val sentCode: SentCode) {
        fun check(code: String) = codeReceiver(code)
    }
    internal var codeHandler: () -> Unit = CodeHandler(sentCode).let {
        {
            do {
                print("Enter code from telegram: ")
                val code = scanner.nextLine()
            } while (!it.check(code))
        }
    }
    fun code(handler: CodeHandler.() -> Unit) = CodeHandler(sentCode).also {
        codeHandler = {
            it.handler()
        }
    }

    inner class PasswordHandler {
        fun check(password: String) = passwordReceiver(password)
    }
    internal var passwordHandler: () -> Unit = PasswordHandler().let {
        {
            do {
                print("Enter account password: ")
                val password = scanner.nextLine()
            } while (!it.check(password))
        }
    }
    fun password(handler: PasswordHandler.() -> Unit) = PasswordHandler().also {
        passwordHandler = {
            it.handler()
        }
    }

    internal var signedHandler: (User) -> Unit = {
        println("Signed in as ${it.firstName}")
    }
    fun signed(handler: (User) -> Unit) {
        signedHandler = handler
    }
}
@file: Suppress("unused")

package com.y9san9.kotlogram.utils

infix fun <T> T.assert(value: T) = this == value || throw AssertionError("$this != $value")
infix fun <T> T.assertNot(value: T) = this != value || throw AssertionError("$this == $value")
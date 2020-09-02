package com.y9san9.kotlogram.utils

internal infix fun <T> T.assert(value: T) = this == value || throw AssertionError("$this != $value")
internal infix fun <T> T.assertNot(value: T) = this != value || throw AssertionError("$this == $value")
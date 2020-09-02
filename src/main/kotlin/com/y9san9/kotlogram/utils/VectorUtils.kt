package com.y9san9.kotlogram.utils

import com.github.badoualy.telegram.tl.core.TLIntVector
import com.github.badoualy.telegram.tl.core.TLVector

internal fun <T> vectorOf(vararg items: T) = TLVector<T>().apply { addAll(items) }
internal fun intVectorOf(vararg ints: Int) = TLIntVector().apply { addAll(ints.toList()) }
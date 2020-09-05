package com.y9san9.kotlogram

import com.y9san9.kotlogram.internal.KotlogramLogger

object KotlogramLogger {
    var enabled: Boolean
        get() = KotlogramLogger.enabled
        set(value) {
            KotlogramLogger.enabled = value
        }
}
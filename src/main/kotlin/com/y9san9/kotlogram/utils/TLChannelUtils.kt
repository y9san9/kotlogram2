package com.y9san9.kotlogram.utils

import com.github.badoualy.telegram.tl.api.TLChannel
import com.github.badoualy.telegram.tl.api.TLInputChannel


internal val TLChannel.input get() = TLInputChannel(id, accessHash)

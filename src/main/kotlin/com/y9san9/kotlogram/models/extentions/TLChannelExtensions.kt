package com.y9san9.kotlogram.models.extentions

import com.github.badoualy.telegram.tl.api.TLChannel
import com.github.badoualy.telegram.tl.api.TLInputChannel


val TLChannel.input get() = TLInputChannel(id, accessHash)

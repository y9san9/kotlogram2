package com.y9san9.kotlogram.models.extentions

import com.github.badoualy.telegram.tl.api.TLInputUser
import com.github.badoualy.telegram.tl.api.TLUser


val TLUser.input get() = TLInputUser(id, accessHash)

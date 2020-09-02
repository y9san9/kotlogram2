package com.y9san9.kotlogram.utils

import com.github.badoualy.telegram.tl.api.TLInputUser
import com.github.badoualy.telegram.tl.api.TLUser


internal val TLUser.input get() = TLInputUser(id, accessHash)

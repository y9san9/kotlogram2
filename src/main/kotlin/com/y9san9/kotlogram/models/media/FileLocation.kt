package com.y9san9.kotlogram.models.media

import com.github.badoualy.telegram.api.utils.toInputFileLocation
import com.github.badoualy.telegram.tl.api.TLAbsFileLocation


class FileLocation(location: TLAbsFileLocation, val size: Int) {
    val input = location.toInputFileLocation()!!
}

fun TLAbsFileLocation.wrap(size: Int) = FileLocation(this, size)

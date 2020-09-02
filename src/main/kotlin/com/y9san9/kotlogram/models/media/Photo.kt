package com.y9san9.kotlogram.models.media

import com.github.badoualy.telegram.api.utils.InputFileLocation
import com.github.badoualy.telegram.tl.api.*
import com.github.badoualy.telegram.tl.core.TLBytes
import com.y9san9.kotlogram.KotlogramClient

class Photo(
        private val client: KotlogramClient, private val id: Long,
        private val accessHash: Long, val sizes: List<PhotoSize>
) {
    fun download(size: PhotoSize) = client.download(this, size)
}

fun TLAbsPhoto.wrap(client: KotlogramClient): Photo? {
    return Photo(client, asPhoto.id, asPhoto.accessHash, asPhoto.sizes.map { it.wrap(client) ?: return null })
}

class PhotoSize (
        val client: KotlogramClient,
        val width: Int,
        val height: Int,
        val fileLocation: FileLocation,
        private val bytes: TLBytes? = null
) {
    val size = fileLocation.size
    fun download() = bytes?.data ?: client.download(fileLocation)
}

fun TLAbsPhotoSize.wrap(client: KotlogramClient) = when(this){
    is TLPhotoSize -> PhotoSize(client, w, h, location.wrap(size))
    is TLPhotoCachedSize -> PhotoSize(client, w, h, location.wrap(bytes.length), bytes)
    else -> null
}

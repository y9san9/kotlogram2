package com.y9san9.kotlogram.models.media


import com.github.badoualy.telegram.tl.api.*
import com.y9san9.kotlogram.KotlogramClient

sealed class Media {
    object Unsupported : Media()
}

data class Contact (
    val phone: String,
    val firstName: String,
    val lastName: String?,
    val userId: Int,
) : Media()

data class DocumentMedia (
    val caption: String,
    val document: Document
) : Media()

data class Game (
    val shortName: String,
    val title: String,
    val description: String,
    val photo: TLAbsPhoto,
    val document: Document,
) : Media()

data class Geo (
    val longitude: Double,
    val latitude: Double
) : Media()

data class PhotoMedia (
    val caption: String,
    val photo: Photo
) : Media()


fun TLAbsMessageMedia.wrap(client: KotlogramClient): Media? {
    return when(this) {
        is TLMessageMediaContact -> Contact(phoneNumber, firstName, lastName, userId)
        is TLMessageMediaDocument -> DocumentMedia(caption, document.wrap(client) ?: return null)
        is TLMessageMediaGame -> Game(
                game.shortName, game.title, game.description,
                game.photo, game.document.wrap(client) ?: return null
        )
        is TLMessageMediaGeo -> Geo(geo.asGeoPoint.long, geo.asGeoPoint.lat)
        is TLMessageMediaPhoto -> PhotoMedia(caption, photo.wrap(client) ?: return null)
        else -> null
    }
}

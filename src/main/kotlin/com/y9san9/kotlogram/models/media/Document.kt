package com.y9san9.kotlogram.models.media

import com.github.badoualy.telegram.api.utils.InputFileLocation
import com.github.badoualy.telegram.api.utils.toInputDocument
import com.github.badoualy.telegram.tl.api.*
import com.y9san9.kotlogram.KotlogramClient
import javax.print.Doc

data class Document (
    val client: KotlogramClient,
    val hash: Long,
    val date: Int,
    val mimeType: String,
    val size: Int,
    val thumb: PhotoSize,
    val version: Int,
    private val attributes: List<DocumentAttribute>,
    val input: InputFileLocation
) {
    enum class DocumentAttribute {
        Animated, Audio, Filename, HasStickers, ImageSize, Sticker, Video
    }
    val animated = attributes.contains(DocumentAttribute.Animated)
    val audio = attributes.contains(DocumentAttribute.Audio)
    val filename = attributes.contains(DocumentAttribute.Filename)
    val hasStickers = attributes.contains(DocumentAttribute.HasStickers)
    val imageSize = attributes.contains(DocumentAttribute.ImageSize)
    val sticker = attributes.contains(DocumentAttribute.Sticker)
    val video = attributes.contains(DocumentAttribute.Video)

    fun download() = client.download(this)
}
fun TLAbsDocumentAttribute.wrap() = when(this) {
    is TLDocumentAttributeAnimated -> Document.DocumentAttribute.Animated
    is TLDocumentAttributeAudio -> Document.DocumentAttribute.Audio
    is TLDocumentAttributeFilename -> Document.DocumentAttribute.Filename
    is TLDocumentAttributeHasStickers -> Document.DocumentAttribute.HasStickers
    is TLDocumentAttributeImageSize -> Document.DocumentAttribute.ImageSize
    is TLDocumentAttributeSticker -> Document.DocumentAttribute.Sticker
    is TLDocumentAttributeVideo -> Document.DocumentAttribute.Video
    else -> throw UnsupportedOperationException()
}

fun TLAbsDocument.wrap(client: KotlogramClient): Document? {
    return Document(
        client,
        asDocument.accessHash,
        asDocument.date,
        asDocument.mimeType,
        asDocument.size,
        asDocument.thumb.wrap(client) ?: return null,
        asDocument.version,
        asDocument.attributes.map { it.wrap() },
        InputFileLocation(
                TLInputDocumentFileLocation(id, asDocument.accessHash, asDocument.version),
                asDocument.dcId
        )
    )
}

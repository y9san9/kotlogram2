@file: Suppress("SpellCheckingInspection")

package com.y9san9.kotlogram.models

import com.github.badoualy.telegram.api.TelegramApp

data class TelegramApp(
        val apiId: Int,
        val apiHash: String,
        val deviceModel: String = "Web",
        val systemVersion: String = "Kotlogram2",
        val appVersion: String = "Api",
        val langCode: String = "en"
) {
    internal fun toKotlogramApp() = TelegramApp(apiId, apiHash, deviceModel, systemVersion, appVersion, langCode)
}
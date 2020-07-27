package com.y9san9.kotlogram.storage

import com.github.badoualy.telegram.api.TelegramApiStorage
import com.github.badoualy.telegram.mtproto.auth.AuthKey
import com.github.badoualy.telegram.mtproto.model.DataCenter
import com.github.badoualy.telegram.mtproto.model.MTSession
import com.y9san9.kotlogram.utils.refresh
import java.io.File
import java.io.FileNotFoundException


class ApiStorage(name: String = "") : TelegramApiStorage {
    private val authKeyFile = File(System.getProperty("user.dir"),"properties/auth$${name}.key")
    private val nearestDCFile = File(System.getProperty("user.dir"),"properties/dc$${name}.save")

    override fun saveAuthKey(authKey: AuthKey) = authKeyFile.apply { refresh() }.writeBytes(authKey.key)
    override fun loadAuthKey() = try {
        AuthKey(authKeyFile.readBytes())
    } catch (_: FileNotFoundException){
        null
    }

    override fun saveDc(dataCenter: DataCenter) = nearestDCFile.apply { refresh() }.writeText(dataCenter.toString())
    override fun loadDc() = try {
        nearestDCFile.readText().split(":").let { (ip, port) ->
            DataCenter(ip, port.toInt())
        }
    } catch (_: FileNotFoundException) {
        null
    }

    override fun deleteAuthKey() = authKeyFile.deleteRecursively().let {  }
    override fun deleteDc() = nearestDCFile.deleteRecursively().let {  }

    override fun saveSession(session: MTSession?) {}
    override fun loadSession(): MTSession? = null
}
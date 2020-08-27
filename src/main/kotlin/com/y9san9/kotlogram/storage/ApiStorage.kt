package com.y9san9.kotlogram.storage

import com.github.badoualy.telegram.api.TelegramApiStorage
import com.github.badoualy.telegram.mtproto.auth.AuthKey
import com.github.badoualy.telegram.mtproto.model.DataCenter
import com.github.badoualy.telegram.mtproto.model.MTSession
import com.y9san9.kds.KDataStorage
import com.y9san9.kds.commit
import java.io.File

class ApiStorage(private val name: String = "") : TelegramApiStorage {
    private val storage = object : KDataStorage(File(System.getProperty("user.dir"), name)) {
        var authKey by property<ByteArray?>()
        var dataCenterIp by property<String?>()
        var dataCenterPort by property<Int?>()
        var session by property<MTSession?>()
    }

    override fun saveAuthKey(authKey: AuthKey) = storage.commit { this.authKey = authKey.key }
    override fun loadAuthKey() = storage.authKey?.let { AuthKey(it) }

    override fun saveDc(dataCenter: DataCenter) = dataCenter.toString().split(":")
        .let { (ip, port) ->
            storage.commit {
                dataCenterIp = ip
                dataCenterPort = port.toInt()
            }
        }

    override fun loadDc() =
        storage.dataCenterIp?.let { ip -> storage.dataCenterPort?.let { port -> DataCenter(ip, port) } }

    override fun deleteAuthKey() = storage.commit { authKey = null }
    override fun deleteDc() = storage.commit {
        dataCenterIp = null
        dataCenterPort = null
    }

    override fun saveSession(session: MTSession?) = storage.commit {
        this.session = session
    }

    override fun loadSession(): MTSession? = storage.session
}
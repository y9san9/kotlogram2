package com.y9san9.kotlogram.storage

import com.github.badoualy.telegram.api.TelegramApiStorage
import com.github.badoualy.telegram.mtproto.auth.AuthKey
import com.github.badoualy.telegram.mtproto.model.DataCenter
import com.github.badoualy.telegram.mtproto.model.MTSession
import com.y9san9.kds.KDataStorage
import com.y9san9.kds.commit
import java.io.File

//TODO: Remove constructor parameter or add creating custom path feature to kds
class ApiStorage(private val name: String = "") : TelegramApiStorage {
    private val storage = object : KDataStorage(File(System.getProperty("user.dir"), name)) {
        var authKey by property<ByteArray>()
        var dataCenter by property<String>()
        var session by property<MTSession>()
    }

    override fun saveAuthKey(authKey: AuthKey) = storage.commit { this.authKey = authKey.key }
    override fun loadAuthKey() = AuthKey(storage.authKey)

    override fun saveDc(dataCenter: DataCenter) = storage.commit { this.dataCenter = dataCenter.toString() }
    override fun loadDc() = storage.dataCenter.split(":").let { (ip, port) ->
        DataCenter(ip, port.toInt())
    }

    //TODO: Create delete function for kds
    override fun deleteAuthKey() {}
    override fun deleteDc() {}

    override fun saveSession(session: MTSession?) = storage.commit {
        this.session = session!!
    }

    override fun loadSession(): MTSession? = storage.session
}
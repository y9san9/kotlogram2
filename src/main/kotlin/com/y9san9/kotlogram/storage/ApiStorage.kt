package com.y9san9.kotlogram.storage

import com.github.badoualy.telegram.api.TelegramApiStorage
import com.github.badoualy.telegram.mtproto.auth.AuthKey
import com.github.badoualy.telegram.mtproto.model.DataCenter
import com.github.badoualy.telegram.mtproto.model.MTSession
import com.y9san9.kds.KDataStorage
import com.y9san9.kds.commit
import java.io.File


private class Storage(name: String) : KDataStorage(name) {
    var authKey by property<ByteArray?>()
    var dataCenter by property<DataCenter?>()
    var session by property<MTSession?>()
    var selfId by property<Int?>(0)
}

class ApiStorage(name: String = "") : TelegramApiStorage {
    private val storage = Storage(name)

    override fun saveAuthKey(authKey: AuthKey) = storage.commit { this.authKey = authKey.key }
    override fun loadAuthKey() = storage.authKey?.let { AuthKey(it) }

    override fun saveDc(dataCenter: DataCenter) = storage.commit {
        this.dataCenter = dataCenter
    }

    override fun loadDc() = storage.dataCenter

    override fun deleteAuthKey() = storage.commit { authKey = null }
    override fun deleteDc() = storage.commit {
        dataCenter = null
    }

    override fun saveSession(session: MTSession?) = storage.commit {
        this.session = session
    }

    override fun loadSession(): MTSession? = storage.session
}
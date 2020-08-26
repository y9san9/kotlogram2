package com.y9san9.kotlogram.storage

import com.github.badoualy.telegram.api.TelegramApiStorage
import com.github.badoualy.telegram.mtproto.auth.AuthKey
import com.github.badoualy.telegram.mtproto.model.DataCenter
import com.github.badoualy.telegram.mtproto.model.MTSession
import com.y9san9.kds.KDataStorage
import com.y9san9.kds.commit

private object Storage : KDataStorage() {
    var authKey by property<ByteArray>()
    var dataCenter by property<String>()
    var session by property<MTSession>()
}

//TODO: Remove constructor parameter or add creating custom path feature to kds
class ApiStorage(name: String = "") : TelegramApiStorage {
    /*private val authKeyFile = File(System.getProperty("user.dir"), "properties/auth$${name}.key")
    private val nearestDCFile = File(System.getProperty("user.dir"), "properties/dc$${name}.save")*/

    override fun saveAuthKey(authKey: AuthKey) = Storage.commit { this.authKey = authKey.key }
    override fun loadAuthKey() = AuthKey(Storage.authKey)

    override fun saveDc(dataCenter: DataCenter) = Storage.commit { this.dataCenter = dataCenter.toString() }
    override fun loadDc() = Storage.dataCenter.split(":").let { (ip, port) ->
        DataCenter(ip, port.toInt())
    }

    //TODO: Create delete function for kds
    override fun deleteAuthKey() { }
    override fun deleteDc() { }

    override fun saveSession(session: MTSession?) = Storage.commit {
        this.session = session!!
    }
    override fun loadSession(): MTSession? = Storage.session
}
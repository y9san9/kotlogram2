package com.y9san9.kotlogram.updates.handlers

import com.github.badoualy.telegram.tl.api.TLAbsUpdate


class AllHandler(
    handler: EventHandler<TLAbsUpdate>
) : Handler<TLAbsUpdate>(handler){
    override fun mapUpdate(update: TLAbsUpdate) = update
}

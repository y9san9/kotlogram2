package com.y9san9.kotlogram.updates.handlers

import com.github.badoualy.telegram.tl.api.TLAbsUpdate


typealias EventHandler<T> = EventDSL.(T) -> Unit

class EventDSL {
    fun filter(filter: () -> Boolean) {
        if(!filter())
            throw EventFiltered()
    }
    class EventFiltered : IllegalStateException()
}

abstract class Handler<T>(val handler: EventHandler<T>) {
    /**
     * @throws Throwable or
     * @return null if cannot intercept update
     */
    abstract fun mapUpdate(update: TLAbsUpdate) : T
}

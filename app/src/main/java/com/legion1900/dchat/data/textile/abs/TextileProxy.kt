package com.legion1900.dchat.data.textile.abs

import io.reactivex.Single
import io.textile.textile.Textile

interface TextileProxy {
    /**
     * Emits Textile instance when its node is online and ready to use.
     * Call init() to init and launch node
     * */
    val instance: Single<Textile>
    val eventBus: TextileEventBus

    /**
     * Initiate and start Textile node
     * @param path - path to Textile wallet root folder
     * */
    fun init(path: String)

    /**
     * Dispose currently running instance
     * */
    fun dispose()
}

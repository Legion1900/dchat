package com.legion1900.dchat.data.impl

import android.app.Application
import com.legion1900.dchat.BuildConfig
import com.legion1900.dchat.data.abs.NodeStateChanged
import com.legion1900.dchat.data.abs.TextileEventBus
import com.legion1900.dchat.data.abs.TextileEventBus.Companion.getSubject
import com.legion1900.dchat.data.abs.TextileProxy
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.textile.textile.Textile

class TextileProxyImpl(
    override val eventBus: TextileEventBus,
    private val app: Application
) : TextileProxy {
    override val instance: Single<Textile>
        get() = isRunning.filter { it }
            .firstOrError()
            .map { Textile.instance() }

    private val isRunning = BehaviorSubject.create<Boolean>().apply { onNext(false) }

    init {
        attachBus()
        eventBus.getSubject<NodeStateChanged>()
            .map(NodeStateChanged::isRunning)
            .subscribe(isRunning)
    }

    override fun init(path: String) {
        if (isRunning.value == false) {
            val isDebug = BuildConfig.DEBUG
            Textile.launch(app, path, isDebug)
        }
    }

    override fun dispose() {
        Textile.instance().destroy()
        attachBus()
        isRunning.onNext(false)
    }

    private fun attachBus() {
        Textile.instance().addEventListener(eventBus)
    }
}

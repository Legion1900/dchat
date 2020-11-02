package com.legion1900.dchat.data.textile.impl

import android.app.Application
import com.legion1900.dchat.BuildConfig
import com.legion1900.dchat.data.textile.abs.NodeStateChanged
import com.legion1900.dchat.data.textile.abs.TextileEventBus
import com.legion1900.dchat.data.textile.abs.TextileEventBus.Companion.getSubject
import com.legion1900.dchat.data.textile.abs.TextileProxy
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.textile.textile.Textile
import java.util.concurrent.atomic.AtomicBoolean

class TextileProxyImpl(
    override val eventBus: TextileEventBus,
    private val app: Application,
    private val path: String,
    private val isDebug: Boolean
) : TextileProxy {
    override val instance: Single<Textile>
        get() {
            launch()
            return isRunning.filter { it }
                .firstOrError()
                .map { Textile.instance() }
                .subscribeOn(Schedulers.io())
        }

    private val isRunning = BehaviorSubject.create<Boolean>().apply { onNext(false) }

    private val lock = Any()
    private val isLaunched = AtomicBoolean(false)

    init {
        attachBus()
        eventBus.getSubject<NodeStateChanged>()
            .map(NodeStateChanged::isRunning)
            .subscribeOn(Schedulers.io())
            .subscribe(isRunning)
    }

    private fun launch() {
        if (!isLaunched.get()) {
            synchronized(lock) {
                if (!isLaunched.get()) {
                    Textile.launch(app, path, isDebug)
                    isLaunched.set(true)
                }
            }
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

package com.legion1900.dchat.data.impl

import com.legion1900.dchat.data.abs.NodeStateChanged
import com.legion1900.dchat.data.abs.TextileEvent
import com.legion1900.dchat.data.abs.TextileEventBus
import io.reactivex.subjects.PublishSubject
import io.textile.textile.TextileEventListener
import kotlin.reflect.KClass

class TextileEventBusImpl(
    private val globalListener: TextileEventListener = MockListener
) : TextileEventBus, TextileEventListener by globalListener {

    private val subjects = mapOf(
        newSubject<NodeStateChanged>(),
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : TextileEvent> getEventSubject(eventClass: KClass<T>): PublishSubject<T> {
        return (subjects[eventClass]
            ?: throw IllegalArgumentException("Event ${eventClass.simpleName} is not supported"))
            .let { it as PublishSubject<T> }
    }

    override fun nodeOnline() {
        globalListener.nodeOnline()
        routeEvent(NodeStateChanged(true))
    }

    override fun nodeStopped() {
        globalListener.nodeStopped()
        routeEvent(NodeStateChanged(false))
    }

    private inline fun <reified T : TextileEvent> newSubject(): Pair<KClass<T>, PublishSubject<T>> {
        return T::class to PublishSubject.create()
    }

    private inline fun <reified T : TextileEvent> routeEvent(event: T) {
        getEventSubject(T::class).onNext(event)
    }
}

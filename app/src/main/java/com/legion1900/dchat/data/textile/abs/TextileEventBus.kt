package com.legion1900.dchat.data.textile.abs

import io.reactivex.subjects.PublishSubject
import io.textile.textile.TextileEventListener
import kotlin.reflect.KClass

interface TextileEventBus : TextileEventListener {
    fun <T : TextileEvent> getEventSubject(eventClass: KClass<T>): PublishSubject<T>

    companion object {
        inline fun <reified T : TextileEvent> TextileEventBus.getSubject(): PublishSubject<T> {
            return getEventSubject(T::class)
        }
    }
}

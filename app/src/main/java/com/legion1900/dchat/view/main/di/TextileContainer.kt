package com.legion1900.dchat.view.main.di

import kotlin.reflect.KClass

class TextileContainer : Container {
    override fun <T : Any> resolve(klass: KClass<T>): T? {
        return null
    }
}

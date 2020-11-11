package com.legion1900.dchat.view.main.di

import kotlin.reflect.KClass

interface Container {
    fun <T : Any> resolve(klass: KClass<T>): T?
}

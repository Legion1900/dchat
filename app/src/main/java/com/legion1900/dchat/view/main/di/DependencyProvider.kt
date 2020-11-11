package com.legion1900.dchat.view.main.di

import kotlin.reflect.KClass

class DependencyProvider(
    vararg providers: Pair<KClass<*>, Provider<*>>
) {
    private val providers = mapOf(*providers)

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any> get(dependency: KClass<T>): T? {
        return providers[dependency]?.provide() as T?
    }
}

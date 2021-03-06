package com.legion1900.dchat.view.main.di

import androidx.lifecycle.ViewModelProvider
import com.legion1900.dchat.view.main.MainViewModel
import com.legion1900.dchat.view.main.di.providers.mainVmProvider
import com.legion1900.dchat.view.main.di.providers.viewModelFactoryProvider
import com.legion1900.dchat.view.main.navigation.DirectionProvider
import kotlin.reflect.KClass

class ActivityContainer : Container {

    private var directionProvider: DirectionProvider? = null
    private var appContainer: AppContainer? = null

    private val dependencyProvider by lazy {
        DependencyProvider(
            ViewModelProvider.Factory::class to viewModelFactoryProvider(
                MainViewModel::class.java to mainVmProvider(demandFromParent()),
            ),
            DirectionProvider::class to Provider { directionProvider }
        )
    }

    fun directionProvider(create: () -> DirectionProvider) {
        directionProvider = create()
    }

    fun appContainer(create: () -> AppContainer) {
        appContainer = create()
    }

    private fun getAppContainer(): AppContainer = appContainer!!

    override fun <T : Any> resolve(klass: KClass<T>): T? {
        var dep = getAppContainer().resolve(klass)
        if (dep == null) {
            dep = dependencyProvider[klass]
        }
        return dep
    }

    private inline fun <reified T : Any> resolveFromParent(): T? {
        return getAppContainer().resolve(T::class)
    }

    private inline fun <reified T : Any> demandFromParent() = resolveFromParent<T>()
        ?: throw Exception("${appContainer!!::class} can't provide instance of ${T::class.qualifiedName}")
}

fun activityContainer(init: ActivityContainer.() -> Unit): ActivityContainer {
    val container = ActivityContainer()
    container.init()
    return container
}

package com.legion1900.dchat.view.main.di

import com.legion1900.dchat.data.textile.abs.TextileEventBus
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.view.main.di.providers.proxyProvider
import com.legion1900.dchat.view.main.di.providers.registrationManagerProvider
import com.legion1900.dchat.view.main.di.providers.textileEventBusProvider
import kotlin.reflect.KClass

class ChatContainer(
    private val appContainer: AppContainer
) : Container {

    private val path get() = "${appContainer.application.filesDir}/textile"

    private val dependencyProvider = DependencyProvider(
        TextileProxy::class to proxyProvider(
            appContainer::application,
            { path },
            { resolve()!! },
            { appContainer.isDebug }
        ),

        TextileEventBus::class to textileEventBusProvider { appContainer.isDebug },

        RegistrationManager::class to registrationManagerProvider(
            { appContainer.isDebug },
            { false },
            { path }
        )
    )

    override fun <T : Any> resolve(klass: KClass<T>): T? = dependencyProvider[klass]

    private inline fun <reified T : Any> resolve() = resolve(T::class)
}

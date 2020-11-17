package com.legion1900.dchat.view.main.di

import com.google.gson.Gson
import com.legion1900.dchat.data.chat.abs.JsonSchemaReader
import com.legion1900.dchat.data.textile.abs.TextileEventBus
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.data.textile.abs.ThreadFileRepo
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.domain.chat.ChatRepo
import com.legion1900.dchat.view.main.di.providers.*
import kotlin.reflect.KClass

class ChatContainer(
    private val appContainer: AppContainer
) : Container {

    private val gson by lazy(LazyThreadSafetyMode.NONE) { Gson() }

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
        ),

        ProfileManager::class to profileManagerProvider { resolve()!! },

        JsonSchemaReader::class to jsonSchemaReaderProvider { appContainer.application },

        ThreadFileRepo::class to threadFileRepoProvider({ resolve()!! }, { gson }),

        ChatRepo::class to chatRepoProvider(
            { resolve()!! },
            { resolve()!! },
            { resolve()!! },
            { resolve()!! }
        )
    )

    override fun <T : Any> resolve(klass: KClass<T>): T? = dependencyProvider[klass]

    private inline fun <reified T : Any> resolve() = resolve(T::class)
}

package com.legion1900.dchat.view.main.di

import android.app.Application
import com.legion1900.dchat.domain.app.AppStateRepo
import com.legion1900.dchat.view.main.di.providers.appStateRepoProvider
import kotlin.reflect.KClass

class AppContainer(val application: Application) : Container {

    private val dependencyProvider by lazy {
        DependencyProvider(
            AppStateRepo::class to appStateRepoProvider(application)
        )
    }

    override fun <T : Any> resolve(klass: KClass<T>): T? {
        return dependencyProvider[klass]
    }
}

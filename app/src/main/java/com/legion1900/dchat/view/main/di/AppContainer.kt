package com.legion1900.dchat.view.main.di

import android.app.Application
import com.legion1900.dchat.BuildConfig
import com.legion1900.dchat.domain.app.AppStateRepo
import com.legion1900.dchat.domain.app.TmpFileRepo
import com.legion1900.dchat.view.main.di.providers.appStateRepoProvider
import com.legion1900.dchat.view.main.di.providers.tmpFileRepoProvider
import kotlin.reflect.KClass

class AppContainer(val application: Application) : Container {

    val isDebug = BuildConfig.DEBUG

    private val dependencyProvider by lazy {
        DependencyProvider(
            AppStateRepo::class to appStateRepoProvider(application),
            Application::class to Provider { application },
            TmpFileRepo::class to tmpFileRepoProvider { application.filesDir.absolutePath }
        )
    }

    override fun <T : Any> resolve(klass: KClass<T>): T? {
        return dependencyProvider[klass]
    }
}

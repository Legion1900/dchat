package com.legion1900.dchat.view.main.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.legion1900.dchat.BuildConfig
import com.legion1900.dchat.domain.account.MnemonicGenerator
import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.view.auth.signup.createmnemonic.CreateMnemonicViewModel
import com.legion1900.dchat.view.auth.signup.profile.CreateProfileViewModel
import com.legion1900.dchat.view.main.di.providers.*
import kotlin.reflect.KClass

class FragmentContainer(
    private val activityContainer: ActivityContainer,
    private val vmClass: Class<out ViewModel>,
    private val textileContainer: TextileContainer? = null
) : Container {

    private val textilePath =
        "${activityContainer.resolve(Application::class)!!.filesDir.absolutePath}/textile"
    private val isDebug = BuildConfig.DEBUG
    private val isLogToDisk = false

    private val dependencyProvider = DependencyProvider(
        MnemonicGenerator::class to mnemonicGeneratorProvider(),
        RegistrationManager::class to registrationManagerProvider(isDebug, isLogToDisk, textilePath)
    )

    private val vmProvider = DependencyProvider(
        ViewModelProvider.Factory::class to viewModelFactoryProvider(createViewModelProvider())
    )

    override fun <T : Any> resolve(klass: KClass<T>): T? {
        var dep = dependencyProvider[klass]
        if (dep == null) dep = vmProvider[klass]
        if (dep == null) dep = textileContainer?.resolve(klass)
        if (dep == null) dep = activityContainer.resolve(klass)
        return dep
    }

    private fun createViewModelProvider(): Pair<Class<out ViewModel>, Provider<out ViewModel>> {
        return when (vmClass) {
            CreateMnemonicViewModel::class.java -> getPair {
                createMnemonicVmProvider(
                    dependencyProvider[MnemonicGenerator::class]!!
                )
            }
            CreateProfileViewModel::class.java -> getPair {
                createProfileVmProvider(dependencyProvider[RegistrationManager::class]!!)
            }
            else -> throw Exception("Can not create requested ViewModel ${vmClass.name}")
        }
    }

    private inline fun <reified T : ViewModel> getPair(
        crossinline newVmProvider: () -> Provider<T>
    ): Pair<Class<T>, Provider<T>> {
        return T::class.java to newVmProvider()
    }
}

package com.legion1900.dchat.view.main.di.providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.legion1900.dchat.view.main.ViewModelFactory
import com.legion1900.dchat.view.main.di.Provider

fun viewModelFactoryProvider(
    vararg pairs: Pair<Class<out ViewModel>, Provider<out ViewModel>>
): Provider<ViewModelProvider.Factory> {
    return Provider {
        val map = mapOf(*pairs)
        ViewModelFactory(map)
    }
}

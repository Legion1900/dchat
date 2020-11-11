package com.legion1900.dchat.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.legion1900.dchat.view.main.di.Provider

class ViewModelFactory(
    private val providers: Map<Class<out ViewModel>, Provider<out ViewModel>>
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return providers[modelClass]?.provide() as T
            ?: throw Exception("No ViewModel provider for class ${modelClass.name} found")
    }
}

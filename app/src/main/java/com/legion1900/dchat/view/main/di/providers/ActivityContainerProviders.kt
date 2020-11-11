package com.legion1900.dchat.view.main.di.providers

import com.legion1900.dchat.domain.app.AppStateRepo
import com.legion1900.dchat.view.main.MainViewModel
import com.legion1900.dchat.view.main.di.Provider

fun mainVmProvider(appStateRepo: AppStateRepo): Provider<MainViewModel> {
    return Provider { MainViewModel(appStateRepo) }
}

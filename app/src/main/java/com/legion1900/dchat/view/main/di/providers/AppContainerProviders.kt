package com.legion1900.dchat.view.main.di.providers

import android.app.Application
import android.content.Context
import com.legion1900.dchat.data.app.SharedPreferAppStateRepo
import com.legion1900.dchat.data.app.TmpFileRepoImpl
import com.legion1900.dchat.domain.app.AppStateRepo
import com.legion1900.dchat.domain.app.TmpFileRepo
import com.legion1900.dchat.view.main.di.Provider

fun appStateRepoProvider(app: Application): Provider<AppStateRepo> {
    return Provider {
        val appStateKey = "AppState"
        val sharedPref = app.getSharedPreferences(appStateKey, Context.MODE_PRIVATE)
        SharedPreferAppStateRepo(sharedPref)
    }
}

fun tmpFileRepoProvider(rootPath: () -> String): Provider<TmpFileRepo> = Provider {
    TmpFileRepoImpl(rootPath())
}

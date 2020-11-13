package com.legion1900.dchat.view.main

import android.app.Application
import com.legion1900.dchat.view.main.di.ActivityContainer
import com.legion1900.dchat.view.main.di.AppContainer
import com.legion1900.dchat.view.main.di.FragmentContainer

class ChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(this)
    }

    companion object {
        lateinit var appContainer: AppContainer
            private set

        var activityContainer: ActivityContainer? = null
        var fragmentContainer: FragmentContainer? = null
    }
}

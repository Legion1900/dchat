package com.legion1900.dchat.view.main

import android.app.Application
import androidx.lifecycle.ViewModel
import com.legion1900.dchat.view.main.di.ActivityContainer
import com.legion1900.dchat.view.main.di.AppContainer
import com.legion1900.dchat.view.main.di.ChatContainer
import com.legion1900.dchat.view.main.di.FragmentContainer

class ChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer(this)
        chatContainer = ChatContainer(appContainer)
    }

    companion object {
        lateinit var appContainer: AppContainer
            private set

        var activityContainer: ActivityContainer? = null
        private lateinit var chatContainer: ChatContainer

        fun newFragmentContainer(vmClass: Class<out ViewModel>): FragmentContainer {
            return FragmentContainer(activityContainer!!, vmClass, chatContainer)
        }
    }
}

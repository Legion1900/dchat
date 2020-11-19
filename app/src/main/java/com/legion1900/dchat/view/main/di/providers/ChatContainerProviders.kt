package com.legion1900.dchat.view.main.di.providers

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.legion1900.dchat.data.account.TextileProfileManager
import com.legion1900.dchat.data.account.TextileRegistrationManager
import com.legion1900.dchat.data.chat.TextileChatRepo
import com.legion1900.dchat.data.chat.abs.JsonSchemaReader
import com.legion1900.dchat.data.chat.impl.JsonSchemaReaderImpl
import com.legion1900.dchat.data.media.TextilePhotoRepo
import com.legion1900.dchat.data.textile.abs.TextileEventBus
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.data.textile.abs.ThreadFileRepo
import com.legion1900.dchat.data.textile.impl.*
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.domain.chat.ChatRepo
import com.legion1900.dchat.domain.media.PhotoRepo
import com.legion1900.dchat.view.main.di.Provider
import io.textile.textile.TextileEventListener
import io.textile.textile.TextileLoggingListener

fun proxyProvider(
    provideApp: () -> Application,
    providePath: () -> String,
    provideBus: () -> TextileEventBus,
    provideDebug: () -> Boolean
): Provider<TextileProxy> {
    return object : Provider<TextileProxy> {
        private var proxy: TextileProxy? = null

        override fun provide(): TextileProxy {
            if (proxy == null) {
                val bus = provideBus()
                val app = provideApp()
                val path = providePath()
                val isDebug = provideDebug()
                proxy = TextileProxyImpl(bus, app, path, isDebug)
            }
            return proxy!!
        }
    }
}

fun textileEventBusProvider(isDebug: () -> Boolean): Provider<TextileEventBus> {
    return Provider {
        val listeners = mutableListOf<TextileEventListener>(AutoInviteHandler())
        if (isDebug()) listeners += TextileLoggingListener()
        val compositeListener = CompositeListener(*listeners.toTypedArray())
        TextileEventBusImpl(compositeListener)
    }
}

fun registrationManagerProvider(
    isDebug: () -> Boolean,
    isLogToDisk: () -> Boolean,
    path: () -> String
): Provider<RegistrationManager> {
    return Provider { TextileRegistrationManager(path(), isDebug(), isLogToDisk()) }
}

fun profileManagerProvider(proxy: () -> TextileProxy): Provider<ProfileManager> {
    return Provider { TextileProfileManager(proxy()) }
}

fun jsonSchemaReaderProvider(ctx: () -> Context): Provider<JsonSchemaReader> {
    return Provider { JsonSchemaReaderImpl(ctx()) }
}

fun threadFileRepoProvider(proxy: () -> TextileProxy, gson: () -> Gson): Provider<ThreadFileRepo> {
    return Provider { ThreadFileRepoImpl(proxy(), gson()) }
}

fun chatRepoProvider(
    proxy: () -> TextileProxy,
    schemaReader: () -> JsonSchemaReader,
    fileRepo: () -> ThreadFileRepo,
    profileManager: () -> ProfileManager
): Provider<ChatRepo> {
    return Provider { TextileChatRepo(proxy(), schemaReader(), fileRepo(), profileManager()) }
}

fun photoRepoProvider(proxy: () -> TextileProxy): Provider<PhotoRepo> {
    return Provider { TextilePhotoRepo(proxy()) }
}

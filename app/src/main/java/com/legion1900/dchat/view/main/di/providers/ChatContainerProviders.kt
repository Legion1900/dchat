package com.legion1900.dchat.view.main.di.providers

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.legion1900.dchat.data.account.TextileProfileManager
import com.legion1900.dchat.data.account.TextileRegistrationManager
import com.legion1900.dchat.data.chat.*
import com.legion1900.dchat.data.chat.abs.ChatModelConverter
import com.legion1900.dchat.data.chat.abs.JsonSchemaReader
import com.legion1900.dchat.data.chat.impl.ChatModelConverterImpl
import com.legion1900.dchat.data.chat.impl.JsonSchemaReaderImpl
import com.legion1900.dchat.data.contact.TextileContactManager
import com.legion1900.dchat.data.inbox.TextileInboxManager
import com.legion1900.dchat.data.media.TextilePhotoRepo
import com.legion1900.dchat.data.textile.abs.TextileEventBus
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.data.textile.abs.ThreadFileRepo
import com.legion1900.dchat.data.textile.impl.*
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.account.RegistrationManager
import com.legion1900.dchat.domain.chat.*
import com.legion1900.dchat.domain.contact.ContactManager
import com.legion1900.dchat.domain.inbox.InboxManager
import com.legion1900.dchat.domain.media.PhotoRepo
import com.legion1900.dchat.view.main.di.Provider
import io.textile.textile.BaseTextileEventListener
import io.textile.textile.FeedItemData
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

        listeners += object : BaseTextileEventListener() {
            override fun threadUpdateReceived(threadId: String, feedItemData: FeedItemData) {
                Log.d("enigma", "Thread Update:")
                Log.d("enigma", "block: ${feedItemData.block}")
                Log.d("enigma", ": ${feedItemData.files.filesList.first().file}")
            }
        }

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
    profileManager: () -> ProfileManager,
    converter: () -> ChatModelConverter
): Provider<ChatRepo> {
    return Provider {
        TextileChatRepo(
            proxy(),
            schemaReader(),
            fileRepo(),
            profileManager(),
            converter()
        )
    }
}

fun photoRepoProvider(proxy: () -> TextileProxy): Provider<PhotoRepo> {
    return Provider { TextilePhotoRepo(proxy()) }
}

fun contactManagerProvider(proxy: () -> TextileProxy): Provider<ContactManager> {
    return Provider { TextileContactManager(proxy()) }
}

fun aclManagerProvider(
    proxy: () -> TextileProxy,
    fileRepo: () -> ThreadFileRepo
): Provider<AclManager> {
    return Provider { TextileAclManager(proxy(), fileRepo()) }
}

fun chatManagerProvider(
    proxy: () -> TextileProxy,
    photoRepo: () -> PhotoRepo,
    fileRepo: () -> ThreadFileRepo
): Provider<ChatManager> {
    return Provider { TextileChatManager(proxy(), photoRepo(), fileRepo()) }
}

fun messageManagerProvider(
    threadFileRepo: () -> ThreadFileRepo,
    proxy: () -> TextileProxy,
    photoRepo: () -> PhotoRepo,
    profileManager: () -> ProfileManager
): Provider<MessageManager> {
    return Provider {
        TextileMessageManager(
            threadFileRepo(),
            proxy(),
            photoRepo(),
            profileManager()
        )
    }
}

fun chatModelConverterProvider(fileRepo: () -> ThreadFileRepo): Provider<ChatModelConverter> {
    return Provider { ChatModelConverterImpl(fileRepo()) }
}

fun messageEventBusProvider(
    proxy: () -> TextileProxy,
    threadFileRepo: () -> ThreadFileRepo
): Provider<MessageEventBus> {
    return Provider { TextileMessageBus(proxy(), threadFileRepo()) }
}

fun inboxManagerProvider(proxy: () -> TextileProxy): Provider<InboxManager> {
    return Provider { TextileInboxManager(proxy()) }
}

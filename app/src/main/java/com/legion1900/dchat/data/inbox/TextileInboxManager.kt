package com.legion1900.dchat.data.inbox

import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.data.textile.errorHandler
import com.legion1900.dchat.domain.inbox.InboxManager
import io.reactivex.Completable

class TextileInboxManager(private val proxy: TextileProxy) : InboxManager {
    override fun registerInbox(address: String, token: String): Completable {
        return proxy.instance.flatMapCompletable { api ->
            Completable.create { emitter ->
                val handler = errorHandler(emitter::onComplete, emitter::onError)
                api.cafes.register(address, token, handler)
            }
        }
    }
}

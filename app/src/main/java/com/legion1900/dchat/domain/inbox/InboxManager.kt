package com.legion1900.dchat.domain.inbox

import io.reactivex.Completable

interface InboxManager {
    /**
     * @param address - address of inbox
     * @param token - secret to access this inbox
     * */
    fun registerInbox(address: String, token: String): Completable
}

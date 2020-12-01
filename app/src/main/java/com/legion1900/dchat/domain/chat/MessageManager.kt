package com.legion1900.dchat.domain.chat

import io.reactivex.Completable

interface MessageManager {
    fun sendMessage(chatId: String, msg: SendMessage): Completable
}

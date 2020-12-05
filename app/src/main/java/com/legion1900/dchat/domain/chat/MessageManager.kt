package com.legion1900.dchat.domain.chat

import com.legion1900.dchat.domain.dto.message.Message
import io.reactivex.Completable
import io.reactivex.Flowable

interface MessageManager {
    fun sendMessage(chatId: String, msg: SendMessage): Completable
    fun deleteMessage(id: String): Completable
    fun getMessages(chatId: String): Flowable<Message>
}

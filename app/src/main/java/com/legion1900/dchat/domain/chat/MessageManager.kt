package com.legion1900.dchat.domain.chat

import com.legion1900.dchat.domain.dto.message.Message
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface MessageManager {
    fun sendMessage(chatId: String, msg: SendMessage): Single<String>
    fun deleteMessage(id: String): Completable
    fun getMessages(chatId: String): Flowable<Message>
}

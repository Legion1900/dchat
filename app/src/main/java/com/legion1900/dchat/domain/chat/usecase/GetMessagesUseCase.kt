package com.legion1900.dchat.domain.chat.usecase

import com.legion1900.dchat.domain.dto.message.MessageModel
import io.reactivex.Flowable

interface GetMessagesUseCase {
    fun getMessages(chatId: String): Flowable<MessageModel>
}

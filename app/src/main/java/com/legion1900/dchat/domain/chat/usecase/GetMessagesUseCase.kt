package com.legion1900.dchat.domain.chat.usecase

import com.legion1900.dchat.domain.dto.message.MessageModelEvent
import io.reactivex.Flowable

interface GetMessagesUseCase {
    fun getMessages(chatId: String): Flowable<MessageModelEvent>
}

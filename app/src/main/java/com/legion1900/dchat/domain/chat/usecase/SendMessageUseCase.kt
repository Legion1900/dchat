package com.legion1900.dchat.domain.chat.usecase

import io.reactivex.Completable

interface SendMessageUseCase {
    fun sendMessage(chatId: String, text: String, photo: ByteArray?): Completable
}

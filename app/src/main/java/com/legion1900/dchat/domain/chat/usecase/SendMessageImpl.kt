package com.legion1900.dchat.domain.chat.usecase

import com.legion1900.dchat.domain.chat.MessageManager
import com.legion1900.dchat.domain.chat.SendText
import io.reactivex.Completable
import java.io.InputStream

class SendMessageImpl(private val msgManager: MessageManager) : SendMessageUseCase {
    override fun sendMessage(chatId: String, text: String, photo: ByteArray?): Completable {
        val msg = SendText(text)
        return msgManager.sendMessage(chatId, msg)
            .flatMapCompletable { Completable.complete() }
    }
}

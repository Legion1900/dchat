package com.legion1900.dchat.domain.chat

import io.reactivex.Completable
import java.io.File

interface ChatManager {
    fun setAvatar(chatId: String, file: File): Completable
}

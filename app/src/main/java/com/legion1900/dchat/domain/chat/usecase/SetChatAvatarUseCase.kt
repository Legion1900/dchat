package com.legion1900.dchat.domain.chat.usecase

import io.reactivex.Completable

interface SetChatAvatarUseCase {
    fun setAvatar(chatId: String, avatar: ByteArray, avatarExtension: String): Completable
}

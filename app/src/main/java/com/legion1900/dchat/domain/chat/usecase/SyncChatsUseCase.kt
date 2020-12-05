package com.legion1900.dchat.domain.chat.usecase

import io.reactivex.Completable

interface SyncChatsUseCase {
    fun syncChats(): Completable
}

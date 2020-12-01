package com.legion1900.dchat.domain.chat.usecase

import io.reactivex.Flowable

interface GetChatsUseCase {
    fun getChats(): Flowable<ChatEvent>
}

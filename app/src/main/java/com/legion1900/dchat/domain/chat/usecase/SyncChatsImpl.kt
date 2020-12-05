package com.legion1900.dchat.domain.chat.usecase

import com.legion1900.dchat.domain.chat.ChatRepo
import com.legion1900.dchat.domain.chat.MessageManager
import com.legion1900.dchat.domain.chat.SendText
import io.reactivex.Completable
import io.reactivex.Observable

class SyncChatsImpl(
    private val chatRepo: ChatRepo,
    private val msgManager: MessageManager
) : SyncChatsUseCase {
    override fun syncChats(): Completable {
        return chatRepo.getChatCount()
            .flatMap { cnt -> chatRepo.getChats(0, cnt) }
            .flatMapObservable { Observable.fromIterable(it) }
            .concatMapSingle { msgManager.sendMessage(it.id, SendText(".")) }
            .concatMapCompletable { msgManager.deleteMessage(it) }
    }
}

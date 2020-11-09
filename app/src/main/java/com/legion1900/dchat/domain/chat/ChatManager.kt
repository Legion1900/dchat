package com.legion1900.dchat.domain.chat

import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

interface ChatManager {
    fun setAvatar(chatId: String, file: File): Completable
    fun inviteToChat(userId: String, chatId: String): Completable
    /**
     * @return Single with user IDs
     * */
    fun getChatMembers(chatId: String): Single<List<String>>
}

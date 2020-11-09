package com.legion1900.dchat.domain.chat

import io.reactivex.Completable
import io.reactivex.Single

interface AclManager {
    fun inviteToChat(userId: String, chatId: String): Completable
    /**
     * @return Single with user IDs
     * */
    fun getChatMembers(chatId: String): Single<List<String>>
}

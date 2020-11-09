package com.legion1900.dchat.domain.chat

import android.accounts.Account
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

interface ChatManager {
    fun setAvatar(chatId: String, file: File): Completable
    fun inviteToChat(userId: String, chatId: String): Completable
    fun getChatMembers(chatId: String): Single<List<Account>>
}

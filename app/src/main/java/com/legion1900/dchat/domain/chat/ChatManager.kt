package com.legion1900.dchat.domain.chat

import com.legion1900.dchat.domain.dto.Chat
import io.reactivex.Single
import java.io.File

interface ChatManager {
    fun getChats(offset: String?, limit: Int): Single<List<Chat>>

    /**
     * Creates empty chat
     * @return Single with chat id
     * */
    fun createChat(name: String): Single<String>
}

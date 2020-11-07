package com.legion1900.dchat.domain.chat

import com.legion1900.dchat.domain.dto.Chat
import io.reactivex.Single

interface ChatRepo {

    fun getChatCount(): Single<Int>

    /**
     * @param offset - zero-based offset by index
     * @return empty list when there is no chat in specified range
     * */
    fun getChats(offset: Int, limit: Int): Single<List<Chat>>

    /**
     * Creates empty chat
     * @return Single with chat id
     * */
    fun addNewChat(name: String): Single<String>
}

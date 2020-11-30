package com.legion1900.dchat.domain.chat.usecase

import io.reactivex.Single

interface CreateChatUseCase {
    /**
     * Creates new chat with specified name and adds there specified members
     * @param name - chat name
     * @param memberIds - list of future member ids
     * @return Single with chat id
     * */
    fun createChat(name: String, memberIds: List<String>): Single<String>
}

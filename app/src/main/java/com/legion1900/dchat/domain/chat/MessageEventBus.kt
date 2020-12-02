package com.legion1900.dchat.domain.chat

import com.legion1900.dchat.domain.dto.event.MessageEvent
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject

interface MessageEventBus {
    /**
     * Returns stream of message events (such as new messages or message deletion) for specified
     * chat or for all chats if no chat id is provided
     * @param chatId - id of chat to be listened to
     * @return PublishSubject of MessageEvent
     * */
    fun getEvents(chatId: String? = null): Flowable<MessageEvent>
}

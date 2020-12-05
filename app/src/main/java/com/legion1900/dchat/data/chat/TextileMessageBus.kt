package com.legion1900.dchat.data.chat

import com.legion1900.dchat.data.chat.gson.MessageJson
import com.legion1900.dchat.data.message.converter.MessageModelConverter
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.data.textile.abs.ThreadFileRepo
import com.legion1900.dchat.data.textile.abs.ThreadUpdateReceived
import com.legion1900.dchat.domain.chat.MessageEventBus
import com.legion1900.dchat.domain.dto.event.MessageEvent
import com.legion1900.dchat.domain.dto.event.NewMessage
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.textile.textile.FeedItemType

class TextileMessageBus(
    private val proxy: TextileProxy,
    private val threadFileRepo: ThreadFileRepo
) : MessageEventBus {

    private val msgConverter = MessageModelConverter()

    override fun getEvents(chatId: String?): Flowable<MessageEvent> {
        return proxy.eventBus.getEventSubject(ThreadUpdateReceived::class)
            .filter { event -> chatId?.let { event.threadId == it } ?: true }
            .filter { it.data.type == FeedItemType.FILES }
            .concatMapSingle { event ->
                val blockId = event.data.block
                threadFileRepo.getFile(blockId, MessageJson::class.java)
                    .map { event.threadId to it }
            }.map { (chatId, msg) -> chatId to msgConverter.convert(msg) }
            .map { (chatId, msg) ->
                @Suppress("USELESS_CAST")
                NewMessage(chatId, msg) as MessageEvent
            }
            .toFlowable(BackpressureStrategy.BUFFER)
    }
}

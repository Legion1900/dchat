package com.legion1900.dchat.data.chat

import com.legion1900.dchat.data.chat.gson.MessageJson
import com.legion1900.dchat.data.message.converter.MessageModelConverter
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.data.textile.abs.ThreadFileRepo
import com.legion1900.dchat.data.textile.abs.ThreadUpdateReceived
import com.legion1900.dchat.domain.chat.MessageEventBus
import com.legion1900.dchat.domain.dto.event.MessageDeleted
import com.legion1900.dchat.domain.dto.event.MessageEvent
import com.legion1900.dchat.domain.dto.event.NewMessage
import com.legion1900.dchat.domain.dto.event.NotSupported
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.textile.textile.FeedItemType

class TextileMessageBus(
    private val proxy: TextileProxy,
    private val threadFileRepo: ThreadFileRepo
) : MessageEventBus {

    private val msgConverter = MessageModelConverter()

    override fun getEvents(chatId: String?): Flowable<MessageEvent> {
        return proxy.eventBus.getEventSubject(ThreadUpdateReceived::class)
            .filter { event -> chatId?.let { event.threadId == it } ?: true }
            .filter { ACCEPTED_EVENTS.contains(it.data.type) }
            .concatMapSingle { mapToEvent(it) }
            .toFlowable(BackpressureStrategy.BUFFER)
    }

    private fun mapToEvent(update: ThreadUpdateReceived): Single<MessageEvent> {
        return when (update.data.type) {
            FeedItemType.FILES -> handleNewMessage(update)
            FeedItemType.IGNORE -> handleMessageDeleted(update)
            else -> Single.just(NotSupported)
        }
    }

    private fun handleNewMessage(update: ThreadUpdateReceived): Single<MessageEvent> {
        val blockId = update.data.block
        return threadFileRepo.getFile(blockId, MessageJson::class.java)
            .map { update.threadId to it }
            .map { (chatId, msg) -> chatId to msgConverter.convert(msg) }
            .map { (chatId, msg) -> NewMessage(chatId, msg) as MessageEvent }
            .onErrorReturnItem(NotSupported)
    }

    private fun handleMessageDeleted(update: ThreadUpdateReceived): Single<MessageEvent> {
        val blockId = update.data.ignore.target.block
        return Single.just(MessageDeleted(update.threadId, blockId))
    }

    private companion object {
        val ACCEPTED_EVENTS = setOf(FeedItemType.FILES, FeedItemType.IGNORE)
    }
}

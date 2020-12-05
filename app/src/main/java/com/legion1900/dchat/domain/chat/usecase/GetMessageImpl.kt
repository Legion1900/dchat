package com.legion1900.dchat.domain.chat.usecase

import com.legion1900.dchat.domain.chat.MessageEventBus
import com.legion1900.dchat.domain.chat.MessageManager
import com.legion1900.dchat.domain.contact.ContactManager
import com.legion1900.dchat.domain.dto.event.MessageDeleted
import com.legion1900.dchat.domain.dto.event.NewMessage
import com.legion1900.dchat.domain.dto.event.NotSupported
import com.legion1900.dchat.domain.dto.message.*
import io.reactivex.Flowable
import io.reactivex.Single

class GetMessageImpl(
    private val messageBus: MessageEventBus,
    private val messageManager: MessageManager,
    private val contactManager: ContactManager
) : GetMessagesUseCase {
    override fun getMessages(chatId: String): Flowable<MessageModelEvent> {
        val messages = messageManager.getMessages(chatId).mapToModel()
        val incoming = messageBus.getEvents(chatId)
            .filter { it !is NotSupported }
            .mapEvents()
        return Flowable.concat(messages, incoming)
    }

    private fun Flowable<Message>.mapToModel(): Flowable<NewMessageModelEvent> {
        return concatMapSingle { msg ->
            contactManager.getContact(msg.senderId)
                .map { it.name }
                .toSingle(msg.senderId)
                .map { name ->
                    val model = contentToModel(msg.content)
                    NewMessageModelEvent(msg.id, name, model, msg.timestamp)
                }
        }
    }

    private fun Flowable<com.legion1900.dchat.domain.dto.event.MessageEvent>.mapEvents(): Flowable<MessageModelEvent> {
        return concatMapSingle { event ->
            when (event) {
                is NewMessage -> newMessageEvent(event.msg)
                is MessageDeleted -> newDeletedEvent(event.msgId)
                else -> throw Exception("no way")
            }
        }
    }

    private fun newMessageEvent(msg: Message): Single<MessageModelEvent> {
        return contactManager.getContact(msg.senderId)
            .map { it.name }
            .toSingle(msg.senderId)
            .map { name ->
                val model = contentToModel(msg.content)
                NewMessageModelEvent(msg.id, name, model, msg.timestamp)
            }
    }

    private fun newDeletedEvent(msgId: String): Single<MessageModelEvent> {
        return Single.just(MessageDeletedModelEvent(msgId))
    }

    private fun contentToModel(content: Content): ContentModel {
        return when (content) {
            is TextContent -> TextModel(content.text)
            is MediaContent -> PhotoModel(content.text, null)
        }
    }
}

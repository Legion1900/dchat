package com.legion1900.dchat.domain.chat.usecase

import com.legion1900.dchat.domain.chat.MessageEventBus
import com.legion1900.dchat.domain.chat.MessageManager
import com.legion1900.dchat.domain.contact.ContactManager
import com.legion1900.dchat.domain.dto.event.NewMessage
import com.legion1900.dchat.domain.dto.message.*
import io.reactivex.Flowable

class GetMessageImpl(
    private val messageBus: MessageEventBus,
    private val messageManager: MessageManager,
    private val contactManager: ContactManager
) : GetMessagesUseCase {
    override fun getMessages(chatId: String): Flowable<MessageModel> {
        val messages = messageManager.getMessages(chatId).mapToModel()
        val incoming = messageBus.getEvents(chatId)
            .filter { it is NewMessage }
            .map { it as NewMessage }
            .map { it.msg }
            .mapToModel()
        return Flowable.concat(messages, incoming)
    }

    private fun contentToModel(content: Content): ContentModel {
        return when (content) {
            is TextContent -> TextModel(content.text)
            is MediaContent -> PhotoModel(content.text, null)
        }
    }

    private fun Flowable<Message>.mapToModel(): Flowable<MessageModel> {
        return concatMapSingle { msg ->
            contactManager.getContact(msg.senderId)
                .map { it.name }
                .toSingle(msg.senderId)
                .map { name ->
                    val model = contentToModel(msg.content)
                    MessageModel(name, model, msg.timestamp)
                }
        }
    }
}

package com.legion1900.dchat.domain.chat.usecase

import com.legion1900.dchat.domain.chat.ChatRepo
import com.legion1900.dchat.domain.contact.ContactManager
import com.legion1900.dchat.domain.dto.Chat
import com.legion1900.dchat.domain.dto.chat.ChatModel
import com.legion1900.dchat.domain.dto.chat.LastMessageModel
import com.legion1900.dchat.domain.dto.message.Message
import com.legion1900.dchat.domain.media.PhotoRepo
import com.legion1900.dchat.domain.media.PhotoWidth
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

class GetChatsImpl(
    private val chatRepo: ChatRepo,
    private val photoRepo: PhotoRepo,
    private val contactManager: ContactManager
) : GetChatsUseCase {

    private val contentConverter = ContentConverter()

    override fun getChats(): Flowable<ChatEvent> {
        val getChats = getChatStream()
            .cache()
        val getChatEvents = getChats.mapToModel().toEvent()
        val getAvatarEvents = getChats.getAvatarStream()
        return getChatEvents.concatWith(getAvatarEvents)
    }

    private fun getChatStream(): Flowable<Chat> {
        return chatRepo.getChatCount()
            .flatMapObservable { chatCnt ->
                var offset = 0
                val limit = 10
                val tasks = mutableListOf<Observable<Chat>>()
                while (offset < chatCnt) {
                    tasks += chatRepo.getChats(offset, limit)
                        .flatMapObservable { Observable.fromIterable(it) }
                    offset += limit
                }
                Observable.concat(tasks)
            }.toFlowable(BackpressureStrategy.BUFFER)
    }

    private fun Flowable<Chat>.getAvatarStream(): Flowable<ChatEvent> {
        return flatMapSingle { chat ->
            chat.avatarId?.let { avatarId ->
                photoRepo.getPhoto(avatarId, PhotoWidth.SMALL)
                    .map {
                        @Suppress("USELESS_CAST")
                        ChatAvatarLoaded(chat.id, it) as ChatEvent
                    }
                    .onErrorReturn { ErrorLoadingAvatar(chat.id, avatarId, it) }
            } ?: Single.just(ChatAvatarLoaded(chat.id, byteArrayOf()))
        }.filter { event ->
            if (event is ChatAvatarLoaded) event.avatar.isNotEmpty() else true
        }
    }

    private fun Flowable<Chat>.mapToModel(): Flowable<ChatModel> {
        return flatMapSingle { chat ->
            chat.lastMessage?.let { messageToModel(it) }
                ?.map { chat.toModel(it) } ?: Single.just(chat.toModel())
        }
    }

    private fun Flowable<ChatModel>.toEvent(): Flowable<ChatEvent> = map { NewChat(it) }

    private fun messageToModel(msg: Message): Single<LastMessageModel> {
        return contactManager.getContact(msg.senderId)
            .map { it.name }
            .toSingle(msg.senderId)
            .map { sender ->
                val msgText = contentConverter.convert(msg.content)
                LastMessageModel(msg.senderId, sender, msgText)
            }
    }

    private fun Chat.toModel(
        lastMsg: LastMessageModel? = null,
        avatar: ByteArray? = null
    ) = ChatModel(id, name, avatar, lastMsg)
}

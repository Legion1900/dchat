package com.legion1900.dchat.data.chat

import com.legion1900.dchat.data.chat.gson.ContentJson
import com.legion1900.dchat.data.chat.gson.ContentTypeJson
import com.legion1900.dchat.data.chat.gson.MessageJson
import com.legion1900.dchat.data.message.converter.MessageModelConverter
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.data.textile.abs.ThreadFileRepo
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.chat.MessageManager
import com.legion1900.dchat.domain.chat.SendMessage
import com.legion1900.dchat.domain.chat.SendPhoto
import com.legion1900.dchat.domain.chat.SendText
import com.legion1900.dchat.domain.dto.message.Message
import com.legion1900.dchat.domain.media.PhotoRepo
import io.reactivex.*
import io.reactivex.Observable
import java.util.*

class TextileMessageManager(
    private val threadFileRepo: ThreadFileRepo,
    private val proxy: TextileProxy,
    private val photoRepo: PhotoRepo,
    profileManager: ProfileManager
) : MessageManager {

    private val keyUtil = ChatKeyUtil()

    private val converter = MessageModelConverter()

    private val senderId = profileManager.getCurrentAccount()
        .map { it.id }
        .cache()

    override fun sendMessage(chatId: String, msg: SendMessage): Single<String> {
        return when (msg) {
            is SendText -> sendText(msg, chatId)
            is SendPhoto -> sendPhoto(msg, chatId)
        }
    }

    override fun deleteMessage(id: String): Completable {
        return proxy.instance.flatMapCompletable { api ->
            Completable.fromRunnable { api.ignores.add(id) }
        }
    }

    override fun getMessages(chatId: String): Flowable<Message> {
        return threadFileRepo.getFiles(MessageJson::class.java, chatId, null, 1000)
            .map { it.data }
            .flatMapObservable { Observable.fromIterable(it) }
            .toFlowable(BackpressureStrategy.BUFFER)
            .map { converter.convert(it) }
    }

    private fun sendText(text: SendText, chatId: String): Single<String> {
        return sendMessage(chatId, text.text)
    }

    private fun sendPhoto(photo: SendPhoto, chatId: String): Single<String> {
        return proxy.instance.flatMap { api ->
            val mediaId = keyUtil.getMediaId(api.threads[chatId].key)
            photoRepo.addPhoto(mediaId, photo.file)
        }.flatMap { photoId ->
            val text = photo.text ?: ""
            sendMessage(chatId, text, photoId)
        }
    }

    private fun sendMessage(chatId: String, text: String, photoId: String? = null): Single<String> {
        val type = if (photoId == null) ContentTypeJson.TEXT else ContentTypeJson.PHOTO
        val content = ContentJson(text, photoId)
        return senderId.map { MessageJson(type, content, it, getTimestamp()) }
            .flatMap { threadFileRepo.insertData(it, chatId) }
    }

    private fun getTimestamp() = Calendar.getInstance().time.time
}

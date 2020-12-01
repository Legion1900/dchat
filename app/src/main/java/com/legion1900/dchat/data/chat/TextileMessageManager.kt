package com.legion1900.dchat.data.chat

import com.legion1900.dchat.data.chat.gson.ContentJson
import com.legion1900.dchat.data.chat.gson.ContentTypeJson
import com.legion1900.dchat.data.chat.gson.MessageJson
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.data.textile.abs.ThreadFileRepo
import com.legion1900.dchat.domain.account.ProfileManager
import com.legion1900.dchat.domain.chat.MessageManager
import com.legion1900.dchat.domain.chat.SendMessage
import com.legion1900.dchat.domain.chat.SendPhoto
import com.legion1900.dchat.domain.chat.SendText
import com.legion1900.dchat.domain.media.PhotoRepo
import io.reactivex.Completable
import java.util.*

class TextileMessageManager(
    private val threadFileRepo: ThreadFileRepo,
    private val proxy: TextileProxy,
    private val photoRepo: PhotoRepo,
    profileManager: ProfileManager
) : MessageManager {

    private val keyUtil = ChatKeyUtil()

    private val senderId = profileManager.getCurrentAccount()
        .map { it.id }
        .cache()

    override fun sendMessage(chatId: String, msg: SendMessage): Completable {
        return when (msg) {
            is SendText -> sendText(msg, chatId)
            is SendPhoto -> sendPhoto(msg, chatId)
        }
    }

    private fun sendText(text: SendText, chatId: String): Completable {
        return sendMessage(chatId, text.text)
    }

    private fun sendPhoto(photo: SendPhoto, chatId: String): Completable {
        return proxy.instance.flatMap { api ->
            val mediaId = keyUtil.getMediaId(api.threads[chatId].key)
            photoRepo.addPhoto(mediaId, photo.file)
        }.flatMapCompletable { photoId ->
            val text = photo.text ?: ""
            sendMessage(chatId, text, photoId)
        }
    }

    private fun sendMessage(chatId: String, text: String, photoId: String? = null): Completable {
        val type = if (photoId == null) ContentTypeJson.TEXT else ContentTypeJson.PHOTO
        val content = ContentJson(text, photoId)
        return senderId.map { MessageJson(type, content, it, getTimestamp()) }
            .flatMapCompletable { threadFileRepo.insertData(it, chatId) }
    }

    private fun getTimestamp() = Calendar.getInstance().time.time
}

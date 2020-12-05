package com.legion1900.dchat.data.chat

import com.legion1900.dchat.data.chat.gson.AvatarJson
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.data.textile.abs.ThreadFileRepo
import com.legion1900.dchat.domain.chat.ChatManager
import com.legion1900.dchat.domain.media.PhotoRepo
import io.reactivex.Completable
import java.io.File

class TextileChatManager(
    private val proxy: TextileProxy,
    private val photoRepo: PhotoRepo,
    private val fileRepo: ThreadFileRepo
) : ChatManager {

    private val keyUtil = ChatKeyUtil()

    override fun setAvatar(chatId: String, file: File): Completable {
        return proxy.instance
            .map { textile ->
                val chatKey = textile.threads[chatId].key
                val mediaId = keyUtil.getMediaId(chatKey)
                val avatarId = keyUtil.getAvatarId(chatKey)
                mediaId to avatarId
            }.flatMapCompletable { (mediaId, avatarId) ->
                photoRepo.addPhoto(mediaId, file)
                    .flatMap { fileRepo.insertData(AvatarJson(it), avatarId) }
                    .flatMapCompletable { Completable.complete() }
            }
    }
}

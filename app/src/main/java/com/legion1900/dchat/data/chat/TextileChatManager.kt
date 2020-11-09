package com.legion1900.dchat.data.chat

import android.accounts.Account
import com.legion1900.dchat.data.chat.gson.AvatarJson
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.data.textile.abs.ThreadFileRepo
import com.legion1900.dchat.domain.chat.ChatManager
import com.legion1900.dchat.domain.media.PhotoRepo
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File

class TextileChatManager(
    private val proxy: TextileProxy,
    private val photoRepo: PhotoRepo,
    private val fileRepo: ThreadFileRepo
) : ChatManager {

    private val chatKeyUtil = ChatKeyUtil()

    override fun setAvatar(chatId: String, file: File): Completable {
        return proxy.instance
            .map { textile ->
                val chatKey = textile.threads[chatId].key
                val mediaId = chatKeyUtil.getMediaId(chatKey)
                val avatarId = chatKeyUtil.getAvatarId(chatKey)
                mediaId to avatarId
            }.flatMapCompletable { (mediaId, avatarId) ->
                photoRepo.addPhoto(mediaId, file)
                    .flatMapCompletable { fileRepo.insertData(AvatarJson(it), avatarId) }
            }
    }

    override fun inviteToChat(userId: String, chatId: String): Completable {
        TODO("Not yet implemented")
    }

    override fun getChatMembers(chatId: String): Single<List<Account>> {
        TODO("Not yet implemented")
    }
}

package com.legion1900.dchat.data.chat

import com.legion1900.dchat.data.chat.gson.AclJson
import com.legion1900.dchat.data.chat.gson.AvatarJson
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.data.textile.abs.ThreadFileRepo
import com.legion1900.dchat.domain.chat.ChatManager
import com.legion1900.dchat.domain.media.PhotoRepo
import io.reactivex.Completable
import io.reactivex.Single
import io.textile.textile.Textile
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
                    .flatMapCompletable { fileRepo.insertData(AvatarJson(it), avatarId) }
            }
    }

    override fun inviteToChat(userId: String, chatId: String): Completable {
        return proxy.instance.flatMapCompletable { textile ->
            val ids = getChatThreadIds(textile, chatId)
            Completable.fromRunnable {
                ids.forEach { textile.invites.add(it, userId) }
            }
        }.andThen(updateAcl(chatId, userId))
    }

    override fun getChatMembers(chatId: String): Single<List<String>> {
        return proxy.instance
            .map { textile ->
                val key = textile.threads[chatId].key
                val aclId = keyUtil.getAclId(key)
                textile.threads[aclId].id
            }.flatMap { fileRepo.getFiles(AclJson::class.java, it, null, 1) }
            .map { it.data.firstOrNull()?.participants ?: emptyList() }
    }

    /**
     * @return chat thread ids (chat thread, ACL thread, media thread, avatar thread)
     * */
    private fun getChatThreadIds(textile: Textile, chatId: String): List<String> {
        val key = textile.threads[chatId].key
        return keyUtil.run {
            listOf(chatId, getAclId(key), getMediaId(key), getAvatarId(key))
        }
    }

    private fun updateAcl(chatId: String, userId: String): Completable {
        return proxy.instance.flatMapCompletable { textile ->
            val chatKey = textile.threads[chatId].key
            val aclId = keyUtil.getAclId(chatKey)
            fileRepo.getFiles(AclJson::class.java, aclId, null, 1)
                .map { it.data.first().participants }
                .map { it.toMutableList().apply { add(userId) } }
                .flatMapCompletable { fileRepo.insertData(AclJson(it), aclId) }
        }
    }
}

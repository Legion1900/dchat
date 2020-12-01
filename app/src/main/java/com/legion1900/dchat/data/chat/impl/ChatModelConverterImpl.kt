package com.legion1900.dchat.data.chat.impl

import com.legion1900.dchat.data.chat.ChatKeyUtil
import com.legion1900.dchat.data.chat.abs.ChatModelConverter
import com.legion1900.dchat.data.chat.gson.AvatarJson
import com.legion1900.dchat.data.chat.gson.MessageJson
import com.legion1900.dchat.data.message.converter.MessageModelConverter
import com.legion1900.dchat.data.textile.abs.ThreadFileRepo
import com.legion1900.dchat.domain.dto.Chat
import io.reactivex.Single
import io.textile.pb.Model

class ChatModelConverterImpl(
    private val fileRepo: ThreadFileRepo
) : ChatModelConverter {

    private val chatKeyUtil = ChatKeyUtil()

    private val msgConverter = MessageModelConverter()

    override fun convert(chatThread: Model.Thread): Single<Chat> {
        val avatarId = chatKeyUtil.getAvatarId(chatThread.key)
        val avatarHash = getAvatarHash(avatarId)
        val msg = getLastMessage(chatThread.id)
        return Single.zip(avatarHash, msg) { hashList, msgList ->
            val avatar = hashList.firstOrNull()?.chatAvatar
            val lastMsg = msgList.firstOrNull()?.let { msgConverter.convert(it) }
            Chat(chatThread.id, chatThread.name, avatar, lastMsg)
        }
    }

    private fun getAvatarHash(threadId: String): Single<List<AvatarJson>> {
        return fileRepo.getFiles(AvatarJson::class.java, threadId, null, 1)
            .map { it.data }
    }

    private fun getLastMessage(threadId: String): Single<List<MessageJson>> {
        return fileRepo.getFiles(MessageJson::class.java, threadId, null, 1)
            .map { it.data }
    }
}

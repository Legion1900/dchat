package com.legion1900.dchat.domain.chat.usecase

import com.legion1900.dchat.domain.app.TmpFileRepo
import com.legion1900.dchat.domain.chat.ChatManager
import io.reactivex.Completable
import java.util.*

class SetChatAvatarImpl(
    private val fileRepo: TmpFileRepo,
    private val chatManager: ChatManager
) : SetChatAvatarUseCase {
    override fun setAvatar(
        chatId: String,
        avatar: ByteArray,
        avatarExtension: String
    ): Completable {
        return fileRepo.writeFile(avatar, randomName(avatarExtension))
            .flatMapCompletable { file ->
                chatManager.setAvatar(chatId, file)
            }.andThen(fileRepo.deleteAllFiles())
    }

    private fun randomName(ext: String) = "chat_avatar_${UUID.randomUUID()}.$ext"
}

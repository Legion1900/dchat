package com.legion1900.dchat.domain.chat.usecase

import com.legion1900.dchat.domain.chat.AclManager
import com.legion1900.dchat.domain.chat.ChatRepo
import io.reactivex.Completable
import io.reactivex.Single

class CreateChatImpl(
    private val chatRepo: ChatRepo,
    private val aclManager: AclManager
) : CreateChatUseCase {
    override fun createChat(name: String, memberIds: List<String>): Single<String> {
        return chatRepo.addNewChat(name)
            .flatMap { chatId ->
                inviteMembers(chatId, memberIds)
                    .andThen(Single.just(chatId))
            }
    }

    private fun inviteMembers(chatId: String, members: List<String>): Completable {
        val tasks = mutableListOf<Completable>()
        members.forEach { tasks += aclManager.inviteToChat(it, chatId) }
        return Completable.merge(tasks)
    }
}

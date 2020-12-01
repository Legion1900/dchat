package com.legion1900.dchat.domain.chat.usecase

import com.legion1900.dchat.domain.dto.chat.ChatModel

sealed class ChatEvent

data class NewChat(val model: ChatModel) : ChatEvent()

data class ChatAvatarLoaded(val id: String, val avatar: ByteArray) : ChatEvent() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatAvatarLoaded

        if (id != other.id) return false
        if (!avatar.contentEquals(other.avatar)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + avatar.contentHashCode()
        return result
    }
}

data class ErrorLoadingAvatar(
    val chatId: String,
    val avatarId: String,
    val e: Throwable
) : ChatEvent()

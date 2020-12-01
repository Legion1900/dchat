package com.legion1900.dchat.domain.dto.chat

data class ChatModel(
    val id: String,
    val name: String,
    val avatar: ByteArray?,
    val lastMsg: LastMessageModel?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatModel

        if (id != other.id) return false
        if (name != other.name) return false
        if (avatar != null) {
            if (other.avatar == null) return false
            if (!avatar.contentEquals(other.avatar)) return false
        } else if (other.avatar != null) return false
        if (lastMsg != other.lastMsg) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (avatar?.contentHashCode() ?: 0)
        result = 31 * result + lastMsg.hashCode()
        return result
    }
}

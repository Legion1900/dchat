package com.legion1900.dchat.domain.dto.message

sealed class MessageModelEvent

data class NewMessageModelEvent(
    val id: String,
    val senderName: String,
    val content: ContentModel,
    val timestamp: Long
) : MessageModelEvent()

data class MessageDeletedModelEvent(val id: String): MessageModelEvent()

package com.legion1900.dchat.domain.dto.message

data class MessageModel(
    val id: String,
    val senderName: String,
    val content: ContentModel,
    val timestamp: Long
)

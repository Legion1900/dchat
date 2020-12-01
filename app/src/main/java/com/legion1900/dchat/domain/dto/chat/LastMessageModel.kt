package com.legion1900.dchat.domain.dto.chat

data class LastMessageModel(
    val senderId: String,
    val senderName: String,
    val msgText: String
)

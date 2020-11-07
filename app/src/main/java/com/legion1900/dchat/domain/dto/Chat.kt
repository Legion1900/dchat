package com.legion1900.dchat.domain.dto

import com.legion1900.dchat.domain.dto.message.Message

data class Chat(
    val id: String,
    val name: String,
    val avatarId: String?,
    val lastMessage: Message?
)

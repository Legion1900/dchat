package com.legion1900.dchat.domain.dto.message

data class Message(val id: String, val content: Content, val senderId: String, val timestamp: Long)

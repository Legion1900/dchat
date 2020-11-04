package com.legion1900.dchat.domain.dto

data class Chat(
    val id: String,
    val name: String,
    val avatarId: String?,
    val users: List<Account>
)

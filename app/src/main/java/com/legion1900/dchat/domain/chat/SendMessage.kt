package com.legion1900.dchat.domain.chat

import java.io.File

/**
 * Message to be sent
 * */
sealed class SendMessage

data class SendText(val text: String) : SendMessage()

data class SendPhoto(val file: File, val text: String?) : SendMessage()

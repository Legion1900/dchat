package com.legion1900.dchat.domain.dto.event

import com.legion1900.dchat.domain.dto.message.Message

sealed class MessageEvent

data class NewMessage(val chatId: String, val msg: Message) : MessageEvent()

data class MessageDeleted(val chatId: String, val msgId: String) : MessageEvent()

object NotSupported : MessageEvent()

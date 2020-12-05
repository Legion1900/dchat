package com.legion1900.dchat.data.message.converter

import com.legion1900.dchat.data.chat.gson.MessageJson
import com.legion1900.dchat.data.message.converter.content.CompositeContentConverter
import com.legion1900.dchat.domain.dto.message.Message

class MessageModelConverter {

    private val contentConverter = CompositeContentConverter()

    fun convert(messageJson: MessageJson): Message {
        val content = contentConverter.convert(messageJson.contentType, messageJson.content)
        return Message(messageJson.blockId!!, content, messageJson.fromUser, messageJson.timestamp)
    }
}

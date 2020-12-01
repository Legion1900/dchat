package com.legion1900.dchat.domain.chat.usecase

import com.legion1900.dchat.domain.dto.message.Content
import com.legion1900.dchat.domain.dto.message.MediaContent
import com.legion1900.dchat.domain.dto.message.TextContent

class ContentConverter {
    fun convert(content: Content): String {
        return when (content) {
            is TextContent -> content.convert()
            is MediaContent -> "Photo"
        }
    }

    private fun TextContent.convert() = text
}

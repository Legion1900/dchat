package com.legion1900.dchat.data.message.converter.content

import com.legion1900.dchat.data.chat.gson.ContentJson
import com.legion1900.dchat.data.chat.gson.ContentTypeJson
import com.legion1900.dchat.domain.dto.message.Content

class CompositeContentConverter : ContentConverter {
    private val converters = mapOf(
        ContentTypeJson.TEXT to TextContentConverter(),
        ContentTypeJson.PHOTO to PhotoContentConverter(),
    )

    override fun convert(type: ContentTypeJson, content: ContentJson): Content {
        return converters[type]?.convert(type, content)
            ?: throw Exception("No proper converter found for $type")
    }
}

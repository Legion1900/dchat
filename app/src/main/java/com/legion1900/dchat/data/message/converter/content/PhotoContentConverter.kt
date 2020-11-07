package com.legion1900.dchat.data.message.converter.content

import com.legion1900.dchat.data.chat.gson.ContentJson
import com.legion1900.dchat.data.chat.gson.ContentTypeJson
import com.legion1900.dchat.domain.dto.message.Content
import com.legion1900.dchat.domain.dto.message.MediaContent

class PhotoContentConverter : ContentConverter {
    override fun convert(type: ContentTypeJson, content: ContentJson): Content {
        return content.run { MediaContent(text, media!!) }
    }
}

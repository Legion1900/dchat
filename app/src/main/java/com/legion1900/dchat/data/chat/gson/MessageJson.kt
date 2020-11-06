package com.legion1900.dchat.data.chat.gson

import com.google.gson.annotations.SerializedName

data class MessageJson(
    @SerializedName("content_type")
    val contentType: ContentType,
    val content: ContentJson,
    @SerializedName("from_id")
    val fromUser: String
)

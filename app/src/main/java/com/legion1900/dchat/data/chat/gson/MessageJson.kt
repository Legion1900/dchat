package com.legion1900.dchat.data.chat.gson

import com.google.gson.annotations.SerializedName
import com.legion1900.dchat.data.textile.abs.ThreadFile

data class MessageJson(
    @SerializedName("content_type")
    val contentType: ContentTypeJson,
    val content: ContentJson,
    @SerializedName("from_id")
    val fromUser: String,
    @SerializedName("date")
    val timestamp: Long
) : ThreadFile()

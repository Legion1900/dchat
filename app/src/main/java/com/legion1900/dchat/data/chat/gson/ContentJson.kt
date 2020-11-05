package com.legion1900.dchat.data.chat.gson

import com.google.gson.annotations.SerializedName

data class ContentJson(
    @SerializedName("from_id")
    val fromId: String,
    val text: String,
    val media: String?
)

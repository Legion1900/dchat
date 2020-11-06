package com.legion1900.dchat.data.chat.gson

import com.google.gson.annotations.SerializedName

enum class ContentType {
    @SerializedName("text") TEXT,
    @SerializedName("photo") PHOTO
}

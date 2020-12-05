package com.legion1900.dchat.data.chat.gson

import com.google.gson.annotations.SerializedName
import com.legion1900.dchat.data.textile.abs.ThreadFile

data class AvatarJson(@SerializedName("chat_avatar") val chatAvatar: String) : ThreadFile()

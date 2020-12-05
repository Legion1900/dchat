package com.legion1900.dchat.data.chat.gson

import com.legion1900.dchat.data.textile.abs.ThreadFile

data class AclJson(val participants: List<String>) : ThreadFile()

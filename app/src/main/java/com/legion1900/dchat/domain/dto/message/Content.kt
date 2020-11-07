package com.legion1900.dchat.domain.dto.message

sealed class Content

data class TextContent(val text: String) : Content()

data class MediaContent(val text: String, val photoId: String) : Content()

package com.legion1900.dchat.domain.dto.message

sealed class ContentModel

data class TextModel(val text: String) : ContentModel()

data class PhotoModel(val text: String, val photo: ByteArray?) : ContentModel() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PhotoModel

        if (text != other.text) return false
        if (photo != null) {
            if (other.photo == null) return false
            if (!photo.contentEquals(other.photo)) return false
        } else if (other.photo != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + (photo?.contentHashCode() ?: 0)
        return result
    }
}

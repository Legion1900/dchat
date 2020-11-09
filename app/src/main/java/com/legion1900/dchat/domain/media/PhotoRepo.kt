package com.legion1900.dchat.domain.media

import io.reactivex.Single
import java.io.File

interface PhotoRepo {
    fun getPhoto(photoId: String, width: PhotoWidth): Single<ByteArray>
    fun addPhoto(threadId: String, file: File): Single<String>
}

enum class PhotoWidth {
    SMALL, MEDIUM, LARGE
}

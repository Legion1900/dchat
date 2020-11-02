package com.legion1900.dchat.domain.media

import io.reactivex.Single

interface PhotoRepo {
    fun getPhoto(photoId: String, width: PhotoWidth): Single<ByteArray>
}

enum class PhotoWidth {
    SMALL, MEDIUM
}

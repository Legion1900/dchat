package com.legion1900.dchat.data.chat.abs

import io.reactivex.Completable
import io.reactivex.Single

/**
 * Interface for dealing with text files
 * */
interface ThreadFileRepo {
    fun <T> getFiles(
        clazz: Class<T>,
        threadId: String,
        offset: String?,
        limit: Int
    ): Single<ThreadFiles<T>>

    fun <T> insertData(data: T, threadId: String): Completable
}

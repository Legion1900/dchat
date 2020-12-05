package com.legion1900.dchat.data.textile.abs

import io.reactivex.Completable
import io.reactivex.Single

/**
 * Interface for dealing with text files
 * */
interface ThreadFileRepo {
    fun <T : ThreadFile> getFiles(
        clazz: Class<T>,
        threadId: String,
        offset: String?,
        limit: Int
    ): Single<ThreadFiles<T>>

    fun <T : ThreadFile> getFile(blockId: String, clazz: Class<T>): Single<T>

    fun <T : ThreadFile> insertData(data: T, threadId: String): Completable
}

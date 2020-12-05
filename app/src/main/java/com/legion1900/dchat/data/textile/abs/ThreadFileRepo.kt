package com.legion1900.dchat.data.textile.abs

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

    /**
     * @return ID of block to which data were written
     * */
    fun <T : ThreadFile> insertData(data: T, threadId: String): Single<String>
}

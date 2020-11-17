package com.legion1900.dchat.domain.app

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import java.io.File
import java.io.InputStream

interface TmpFileRepo {
    fun writeFile(inputStream: InputStream, name: String): Single<File>
    fun getFileByName(name: String): Maybe<File>
    fun getAllFiles(): Single<List<File>>
    fun deleteFile(name: String): Completable
    fun deleteAllFiles(): Completable
}

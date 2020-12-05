package com.legion1900.dchat.data.textile.impl

import android.util.Base64
import com.google.gson.Gson
import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.data.textile.abs.ThreadFile
import com.legion1900.dchat.data.textile.abs.ThreadFileRepo
import com.legion1900.dchat.data.textile.abs.ThreadFiles
import com.legion1900.dchat.data.textile.blockHandler
import com.legion1900.dchat.data.textile.dataHandler
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class ThreadFileRepoImpl(private val proxy: TextileProxy, private val gson: Gson) : ThreadFileRepo {

    override fun <T : ThreadFile> getFiles(
        clazz: Class<T>,
        threadId: String,
        offset: String?,
        limit: Int
    ): Single<ThreadFiles<T>> {
        return proxy.instance
            // Offset - Model.File.block
            .map { it.files.list(threadId, offset, limit.toLong()).itemsList }
            .flatMap { models ->
                Observable.fromIterable(models)
                    .concatMapSingle { viewFiles ->
                        readFile(viewFiles.getFiles(0).file.hash, clazz)
                            .doOnSuccess { it.blockId = viewFiles.block }
                    }
                    .buffer(limit)
                    .defaultIfEmpty(emptyList())
                    .firstOrError()
                    .map { ThreadFiles(it, models.lastOrNull()?.block) }
            }
    }

    override fun <T : ThreadFile> getFile(blockId: String, clazz: Class<T>): Single<T> {
        return proxy.instance.flatMap { api ->
            val file = api.files.file(blockId).getFiles(0)
            readFile(file.file.hash, clazz)
                .doOnSuccess { it.blockId = blockId }
        }
    }

    override fun <T : ThreadFile> insertData(data: T, threadId: String): Single<String> {
        return proxy.instance.flatMap { textile ->
            Single.create { emitter ->
                val str = gson.toJson(data)
                val encoded = String(Base64.encode(str.toByteArray(), Base64.DEFAULT))
                val handler = blockHandler({ emitter.onSuccess(it!!.id) }, { emitter.onError(it) })
                textile.files.addData(encoded, threadId, null, handler)
            }
        }
    }

    private fun <R> readFile(hash: String, clazz: Class<R>): Single<R> {
        return proxy.instance.flatMap { textile ->
            Single.create { emitter ->
                val handler = dataHandler(
                    { bytes, _ ->
                        emitter.onSuccess(bytesToObject(bytes!!, clazz))
                    },
                    emitter::onError
                )
                textile.files.content(hash, handler)
            }
        }
    }

    private fun <R> bytesToObject(bytes: ByteArray, clazz: Class<R>): R {
        return gson.fromJson(String(bytes), clazz)
    }
}

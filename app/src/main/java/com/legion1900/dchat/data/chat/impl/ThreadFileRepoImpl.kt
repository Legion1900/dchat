package com.legion1900.dchat.data.chat.impl

import android.util.Base64
import com.google.gson.Gson
import com.legion1900.dchat.data.chat.abs.ThreadFileRepo
import com.legion1900.dchat.data.chat.abs.ThreadFiles
import com.legion1900.dchat.data.textile.blockHandler
import com.legion1900.dchat.data.textile.dataHandler
import com.legion1900.dchat.data.textile.abs.TextileProxy
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class ThreadFileRepoImpl(private val proxy: TextileProxy, private val gson: Gson) : ThreadFileRepo {

    override fun <T> getFiles(
        clazz: Class<T>,
        threadId: String,
        offset: String?,
        limit: Int
    ): Single<ThreadFiles<T>> {
        return proxy.instance
            .map { it.files.list(threadId, offset, limit.toLong()).itemsList }
            .flatMap { models ->
                Observable.fromIterable(models)
                    .concatMapSingle { readFile(it.getFiles(0).file.hash, clazz) }
                    .buffer(limit)
                    .firstOrError()
                    .map { ThreadFiles(it, models.last().block) }
            }
    }

    override fun <T> insertData(data: T, threadId: String): Completable {
        return proxy.instance.flatMapCompletable { textile ->
            Completable.create { emitter ->
                val str = gson.toJson(data)
                val encoded = String(Base64.encode(str.toByteArray(), Base64.DEFAULT))
                val handler = blockHandler({ emitter.onComplete() }, { emitter.onError(it) })
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

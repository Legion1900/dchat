package com.legion1900.dchat.data.app

import com.legion1900.dchat.domain.app.TmpFileRepo
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.locks.ReentrantLock

class TmpFileRepoImpl(private val path: String) : TmpFileRepo {

    private val lock = ReentrantLock(true)
    override fun writeFile(data: ByteArray, name: String): Single<File> {
        val file = File(path, name)
        val fout = FileOutputStream(file)
        return Single.create { emitter ->
            try {
                lock.lock()
                fout.write(data)
                emitter.onSuccess(file)
            } catch (e: Exception) {
                emitter.onError(e)
            } finally {
                lock.unlock()
            }
        }
    }

    override fun getFileByName(name: String): Maybe<File> {
        return Maybe.create<File> { emitter ->
            lock.lock()
            val file = File(path, name)
            if (file.exists()) emitter.onSuccess(file)
            else emitter.onComplete()
            lock.unlock()
        }.observeOn(Schedulers.io())
    }

    override fun getAllFiles(): Single<List<File>> {
        return Single.fromCallable {
            try {
                lock.lock()
                val file = File(path)
                file.listFiles()!!.asList()
            } finally {
                lock.unlock()
            }
        }.observeOn(Schedulers.io())
    }

    override fun deleteFile(name: String): Completable {
        return Completable.fromRunnable {
            try {
                lock.lock()
                val file = File(path, name)
                if (!file.exists()) throw FileNotFoundException("File $file not found")
                if (!file.delete()) throw IOException("File $file deletion has failed")
            } finally {
                lock.unlock()
            }
        }.observeOn(Schedulers.io())
    }

    override fun deleteAllFiles(): Completable {
        return getAllFiles().flatMapCompletable { files ->
            Completable.fromRunnable {
                try {
                    lock.lock()
                    files.forEach { it.delete() }
                } finally {
                    lock.unlock()
                }
            }
        }.observeOn(Schedulers.io())
    }
}

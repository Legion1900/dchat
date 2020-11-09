package com.legion1900.dchat.data.media

import com.legion1900.dchat.data.textile.abs.TextileProxy
import com.legion1900.dchat.data.textile.blockHandler
import com.legion1900.dchat.domain.media.PhotoRepo
import com.legion1900.dchat.domain.media.PhotoWidth
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.textile.textile.Handlers
import java.io.File

class TextilePhotoRepo(private val proxy: TextileProxy) : PhotoRepo {
    override fun getPhoto(photoId: String, width: PhotoWidth): Single<ByteArray> {
        return proxy.instance
            .flatMap { textile ->
                Single.create { emitter ->
                    val path = "$photoId/0"
                    val widthVal = getWidthValue(width)
                    val handler = newDataHandler(emitter) {
                        IllegalArgumentException("No photo for path: $path")
                    }
                    textile.files.imageContentForMinWidth(path, widthVal, handler)
                }
            }
    }

    override fun addPhoto(threadId: String, file: File): Single<String> {
        return proxy.instance.flatMap { textile ->
            Single.create { emitter ->
                val handler =
                    blockHandler({ emitter.onSuccess(it!!.data) }, { emitter.onError(it) })
                textile.files.addFiles(file.absolutePath, threadId, null, handler)
            }
        }
    }

    private fun getWidthValue(width: PhotoWidth): Long {
        return when (width) {
            PhotoWidth.SMALL -> 100
            PhotoWidth.MEDIUM -> 320
            PhotoWidth.LARGE -> 800
        }
    }

    private fun newDataHandler(
        emitter: SingleEmitter<ByteArray>,
        onDataNull: () -> Exception
    ): Handlers.DataHandler {
        return object : Handlers.DataHandler {
            override fun onComplete(data: ByteArray?, media: String?) {
                data?.let {
                    if (!emitter.isDisposed) {
                        emitter.onSuccess(data)
                    }
                } ?: onError(onDataNull())
            }

            override fun onError(e: Exception) {
                if (!emitter.isDisposed) {
                    emitter.onError(e)
                }
            }
        }
    }
}

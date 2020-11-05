package com.legion1900.dchat.data.textile

import io.textile.pb.Model
import io.textile.textile.Handlers
import java.lang.Exception

fun dataHandler(
    onComplete: (ByteArray?, String) -> Unit,
    onError: (Exception) -> Unit
): Handlers.DataHandler {
    return object : Handlers.DataHandler {
        override fun onComplete(data: ByteArray?, media: String) {
            onComplete(data, media)
        }

        override fun onError(e: Exception) {
            onError(e)
        }

    }
}

fun blockHandler(
    onComplete: (Model.Block?) -> Unit,
    onError: (Exception) -> Unit
): Handlers.BlockHandler {
    return object : Handlers.BlockHandler {
        override fun onComplete(block: Model.Block?) {
            onComplete(block)
        }

        override fun onError(e: Exception) {
            onError(e)
        }
    }
}

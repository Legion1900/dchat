package com.legion1900.dchat.data.chat.abs

import androidx.annotation.RawRes
import io.reactivex.Single

interface JsonSchemaReader {
    fun readSchema(@RawRes id: Int): Single<String>
}

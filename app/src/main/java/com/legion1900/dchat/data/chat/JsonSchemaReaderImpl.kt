package com.legion1900.dchat.data.chat

import android.content.Context
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.BufferedReader
import java.io.InputStreamReader

class JsonSchemaReaderImpl(context: Context) : JsonSchemaReader {

    private val resources = context.resources

    override fun readSchema(id: Int): Single<String> {
        return Single.fromCallable {
            val builder = StringBuilder()
            val stream = resources.openRawResource(id)
            BufferedReader(InputStreamReader(stream)).use { r ->
                var line: String?
                do {
                    line = r.readLine()
                    line?.let { builder.append(it) }
                } while (line != null)
            }
            builder.toString()
        }.subscribeOn(Schedulers.io())
    }
}

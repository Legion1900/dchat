package com.legion1900.dchat.view.util.ext

import java.io.*

fun Serializable.serialize(): ByteArray {
    val outBytes = ByteArrayOutputStream()
    val outStream = ObjectOutputStream(outBytes)
    try {
        outStream.writeObject(this)
        return outBytes.toByteArray()
    } finally {
        outBytes.close()
        outStream.close()
    }
}

inline fun <reified T> ByteArray.deserialize(): T {
    val inBytes = ByteArrayInputStream(this)
    val inStream = ObjectInputStream(inBytes)
    try {
        return inStream.readObject() as T
    } finally {
        inBytes.close()
        inStream.close()
    }
}

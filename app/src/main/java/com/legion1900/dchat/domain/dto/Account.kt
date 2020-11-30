package com.legion1900.dchat.domain.dto

import android.util.Base64
import com.legion1900.dchat.view.util.ext.deserialize
import com.legion1900.dchat.view.util.ext.serialize
import java.io.Serializable

data class Account(val id: String, val name: String, val avatarId: String) : Serializable

fun Account.asBase64() = Base64.encodeToString(serialize(), Base64.DEFAULT)
fun String.toAccount(): Account {
    val bytes = Base64.decode(this, Base64.DEFAULT)
    return bytes.deserialize()
}

package com.legion1900.dchat.view.util.ext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.fragment.app.Fragment

fun Fragment.copyToClipboard(data: String, label: String? = null) {
    val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, data)
    clipboard.setPrimaryClip(clip)
}

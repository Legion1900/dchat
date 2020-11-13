package com.legion1900.dchat.view.util.views

import android.graphics.Typeface

data class TextParameters(
    val color: Int,
    val typeface: Typeface,
    val fontSize: Float,
) {
    companion object {
        fun defaultTypeface(color: Int, isBold: Boolean, fontSize: Float): TextParameters {
            val typeface = if (isBold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            return TextParameters(color, typeface, fontSize)
        }
    }
}

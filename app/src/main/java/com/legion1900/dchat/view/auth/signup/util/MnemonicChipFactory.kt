package com.legion1900.dchat.view.auth.signup.util

import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.legion1900.dchat.R
import com.legion1900.dchat.view.util.views.ChipTextDrawable
import com.legion1900.dchat.view.util.views.TextParameters

class MnemonicChipFactory(private val fragment: Fragment) {
    fun createChips(words: List<String>, isNumbered: Boolean = true): List<Chip> {
        val bgIcon: Drawable?
        val params: TextParameters?
        if (isNumbered) {
            bgIcon = getChipBgDrawable()
            params = createParams()
        } else {
            bgIcon = null
            params = null
        }
        return internalCreate(words, params, bgIcon)
    }

    private fun getChipBgDrawable(): Drawable {
        return ResourcesCompat.getDrawable(
            fragment.resources,
            R.drawable.chip_icon_bg,
            fragment.requireContext().theme
        )!!
    }

    private fun createParams(): TextParameters {
        val numberSize = fragment.resources.getDimension(R.dimen.default_text) * 0.75f
        val color = ResourcesCompat.getColor(
            fragment.resources,
            R.color.design_default_color_on_primary,
            fragment.requireContext().theme
        )
        return TextParameters.defaultTypeface(color, false, numberSize)
    }

    private fun internalCreate(
        words: List<String>,
        params: TextParameters?,
        bgDrawable: Drawable?
    ): List<Chip> {
        val chips = mutableListOf<Chip>()
        for ((i, word) in words.withIndex()) {
            chips += Chip(fragment.requireContext()).apply {
                val numberDrawable = createTextDrawable((i + 1).toString(), params, bgDrawable)
                chipIconSize
                text = word
                setTextAppearanceResource(R.style.DefaultText)
                numberDrawable?.let { chipIcon = it }
                isClickable = false
            }
        }
        return chips
    }

    private fun createTextDrawable(
        text: String,
        params: TextParameters?,
        bgDrawable: Drawable?
    ): Drawable? {
        return if (params != null && bgDrawable != null) {
            ChipTextDrawable(text, params, bgDrawable)
        } else null
    }
}

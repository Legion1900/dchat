package com.legion1900.dchat.view.util.views

import android.graphics.*
import android.graphics.drawable.Drawable


class ChipTextDrawable(
    val text: String,
    textParams: TextParameters,
    private val bgDrawable: Drawable
) : Drawable() {

    private val textPaint = Paint().apply {
        color = textParams.color
        typeface = textParams.typeface
        textSize = textParams.fontSize
        textAlign = Paint.Align.CENTER
    }

    override fun draw(canvas: Canvas) {
        bgDrawable.draw(canvas)
        val textBounds = Rect()
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        val baseLineY = bounds.bottom - (bounds.height() - textBounds.height()) / 2f
        canvas.drawText(text, bounds.centerX().toFloat(), baseLineY, textPaint)
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        bounds?.let { bgDrawable.bounds = it }
    }

    override fun setAlpha(alpha: Int) {
        textPaint.alpha = alpha
        bgDrawable.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        textPaint.colorFilter = colorFilter
        bgDrawable.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE
}

package com.paramsen.noise.sample.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.lang.System.arraycopy

/**
 * @author Pär Amsen 06/2017
 */
class FFTView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    val fft: FloatArray = FloatArray(4098)
    val paint: Paint = Paint()

    init {
        paint.color = Color.parseColor("#1F77ED")
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.DKGRAY)

        synchronized(fft) {
            fft.forEachIndexed { i, f ->
                canvas.drawPoint(width * (i / fft.size.toFloat()), -(f * 0.01f) + height, paint)
            }
        }
    }

    fun onFFT(fft: FloatArray) {
        synchronized(this.fft) {
            arraycopy(fft, 0, this.fft, 0, fft.size)
            postInvalidate()
        }
    }
}
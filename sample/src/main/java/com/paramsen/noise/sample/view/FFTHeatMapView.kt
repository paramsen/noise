package com.paramsen.noise.sample.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*

/**
 * @author Pär Amsen 06/2017
 */
class FFTHeatMapView(context: Context, attrs: AttributeSet?) : View(context, attrs), FFTView {
    val sec = 10
    val hz = 44100 / 4096
    val history = hz * sec
    val resolution = 32
    val ffts: ArrayDeque<FloatArray> = ArrayDeque()

    val paintBandsFill: Paint = Paint()
    val paintBands: Paint = Paint()
    val paintText: Paint = Paint()

    init {
        paintBandsFill.color = Color.parseColor("#33FF2C00")
        paintBandsFill.style = Paint.Style.FILL

        paintBands.color = Color.parseColor("#FF2C00")
        paintBands.strokeWidth = 1f
        paintBands.style = Paint.Style.STROKE

        paintText.color = Color.parseColor("#AAFFFFFF")
        paintText.style = Paint.Style.FILL
        paintText.textSize = 12f.px
    }

    override fun onDraw(canvas: Canvas) {
        val fftW = width / history.toFloat()
        val bandWH = height / resolution.toFloat()

        var x: Float
        var y: Float
        var band: FloatArray? = null

        for (i in 0..ffts.size - 1) {
            synchronized(ffts) {
                band = ffts.elementAt(i)
            }

            x = width - (fftW * i)

            for (j in 0..resolution - 1) {
                y = height - (bandWH * j)

                paintBandsFill.color = band?.get(j)?.toInt() ?: 0
                canvas.drawRect(x - fftW, y - bandWH, x, y, paintBandsFill)
            }
        }
    }

    override fun onFFT(fft: FloatArray) {
        //zero dc and nyquist
        fft[0] = .0f
        fft[1] = .0f
        fft[fft.size - 2] = .0f
        fft[fft.size - 1] = .0f

        val bands = FloatArray(resolution)
        var accum: Float

        for (i in 0..resolution - 1) {
            accum = .0f

            for (j in 0..fft.size / resolution - 1) {
                accum += fft[i * j]
            }

            accum /= resolution
            bands[i] = accum
        }

        synchronized(ffts) {
            ffts.addFirst(bands)

            while (ffts.size > history)
                ffts.removeLast()
        }

        postInvalidate()
    }
}
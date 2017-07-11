package com.paramsen.noise.sample.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import java.util.*

/**
 * @author Pär Amsen 06/2017
 */
class FFTSpectogramView(context: Context, attrs: AttributeSet?) : SimpleSurface(context, attrs), FFTView {
    val TAG = javaClass.simpleName!!

    val sec = 10
    val hz = 44100 / 4096
    val history = hz * sec
    val resolution = 512
    val ffts: ArrayDeque<FloatArray> = ArrayDeque()

    val paintBandsFill: Paint = Paint()
    val paintText: Paint = Paint()

    val hot = 30000

    init {
        paintBandsFill.color = Color.parseColor("#33FF2C00")
        paintBandsFill.style = Paint.Style.FILL

        paintText.color = Color.parseColor("#AAFFFFFF")
        paintText.style = Paint.Style.FILL
        paintText.textSize = 12f.px
    }

    var min = Float.MAX_VALUE
    var max = Float.MIN_VALUE

    fun drawFFT(canvas: Canvas): Canvas {

        val fftW = width / history.toFloat()
        val bandWH = height / resolution.toFloat()

        var x: Float
        var y: Float
        var band: FloatArray? = null

        canvas.drawColor(Color.rgb(24, 29, 24))
        for (i in 0..ffts.size - 1) {
            synchronized(ffts) {
                band = ffts.elementAt(i)
            }

            x = width - (fftW * i)

            for (j in 0..resolution - 1) {
                y = height - (bandWH * j)
                val mag = band?.get(j) ?: .0f

                paintBandsFill.color = Spectogram.color(Math.min(mag / hot.toDouble(), 1.0))
                canvas.drawRect(x - fftW, y - bandWH, x, y, paintBandsFill)

                /*if (mag > max) {
                    max = mag
                    Log.d(TAG, "=== MAX: " + max.toString())
                }

                if (mag < min) {
                    min = mag
                    Log.d(TAG, "=== MIN: " + min.toString())
                }*/
            }
        }

        for (i in 0..height) {
            val f = i / height.toDouble()
            paintBandsFill.color = Spectogram.color(1.0 - f)

            canvas.drawRect(0f, i.toFloat(), 10f, i + 1f, paintBandsFill)
        }

        canvas.drawText("FFT SPECTOGRAM", 16f.px, 24f.px, paintText)

        return canvas
    }

    override fun onFFT(fft: FloatArray) {
        //zero dc and nyquist
        fft[0] = .0f
        fft[1] = .0f
        fft[fft.size - 2] = .0f
        fft[fft.size - 1] = .0f

        val bands = FloatArray(resolution)
        var accum: Float
        var avg = 0f

        for (i in 0..resolution - 1) {
            accum = .0f

            for (j in 0..fft.size / resolution - 1 step 2) {
                accum += (Math.sqrt(Math.pow(fft[i * j].toDouble(), 2.0) + Math.pow(fft[i * j + 1].toDouble(), 2.0))).toFloat() //magnitudes
            }

            accum /= resolution
            bands[i] = accum
            avg += accum
        }

        avg /= resolution

        for (i in 0..resolution - 1) {
            if (bands[i] < avg / 2) {
                bands[i] * 100000f
            } /*else {
                bands[i] * 10000f
            }*/
        }

        synchronized(ffts) {
            ffts.addFirst(bands)

            while (ffts.size > history)
                ffts.removeLast()
        }

        drawSurface(this::drawFFT)
    }


}
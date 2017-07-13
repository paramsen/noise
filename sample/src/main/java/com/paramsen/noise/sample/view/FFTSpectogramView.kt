package com.paramsen.noise.sample.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import java.util.*
import kotlin.system.measureTimeMillis

/**
 * @author Pär Amsen 06/2017
 */
class FFTSpectogramView(context: Context, attrs: AttributeSet?) : SimpleSurface(context, attrs), FFTView {
    val TAG = javaClass.simpleName!!

    val sec = 10
    val hz = 44100 / 4096
    val fps = 1000 / hz
    val history = hz * sec
    var resolution = 512
    val ffts = ArrayDeque<FloatArray>()

    val paintSpectogram: Paint = Paint()
    val bg: Paint = Paint()
    val paintText: Paint = textPaint()
    val paintMsg: Paint = errTextPaint()

    val hot = 50000

    val drawTimes = ArrayDeque<Long>()
    var msg: Pair<Long, String>? = null

    init {
        paintSpectogram.color = Color.parseColor("#FF2C00")
        paintSpectogram.style = Paint.Style.FILL

        bg.color = Color.RED
        bg.style = Paint.Style.FILL_AND_STROKE
        bg.strokeWidth = 10f
    }

    fun drawFFT(canvas: Canvas): Canvas {
        // If rendering is causing backpressure [and thus fps drop], lower resolution + show message
        // ignore if downsampling goes too far (< 32)
        if (resolution > 32 && drawTimes.size >= history / 4 && drawTimes.sum().div(drawTimes.size) > fps) {
            Log.w(TAG, "Draw hz exceeded 60")
            synchronized(ffts) {
                ffts.clear()
                drawTimes.clear()
                resolution /= 2

                msg = Pair(System.currentTimeMillis() + 10000, "DOWNSAMPLE DUE TO LOW GPU MEMORY")
            }

            return canvas
        }

        drawTimes.addLast(measureTimeMillis {
            val fftW = width / history.toFloat()
            val bandWH = height / resolution.toFloat()

            var x: Float
            var y: Float
            var band: FloatArray? = null

            canvas.drawColor(Color.rgb(20, 20, 25))
            for (i in 0..ffts.size - 1) {
                synchronized(ffts) {
                    band = ffts.elementAtOrNull(i)
                }

                x = width - (fftW * i)

                for (j in 0..resolution - 1) {
                    y = height - (bandWH * j)
                    val mag = band?.get(j) ?: .0f

                    paintSpectogram.color = Spectogram.color(Math.min(mag / hot.toDouble(), 1.0))
                    canvas.drawRect(x - fftW, y - bandWH, x, y, paintSpectogram)
                }
            }

            for (i in 0..height) {
                val f = i / height.toDouble()
                paintSpectogram.color = Spectogram.color(1.0 - f)

                canvas.drawRect(0f, i.toFloat(), 10f, i + 1f, paintSpectogram)
            }

            canvas.drawText("FFT SPECTOGRAM", 16f.px, 24f.px, paintText)

            if (msg?.first ?: 0 > System.currentTimeMillis()) {
                canvas.drawText(msg?.second, (width - paintMsg.measureText(msg?.second)) / 2, height - 16f.px, paintMsg)
            }
        })

        while (drawTimes.size > history) drawTimes.removeFirst()

        return canvas
    }

    override fun onFFT(fft: FloatArray) {
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
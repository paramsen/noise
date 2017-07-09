package com.paramsen.noise.sample.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Pär Amsen 06/2017
 */
class FFTHeatMapView(context: Context, attrs: AttributeSet?) : SurfaceView(context, attrs), FFTView {
    val TAG = javaClass.simpleName!!

    val sec = 10
    val hz = 44100 / 4096
    val history = hz * sec
    val resolution = 256
    val ffts: ArrayDeque<FloatArray> = ArrayDeque()

    val active = AtomicBoolean(false)

    val paintBandsFill: Paint = Paint()
    val bg: Paint = Paint()
    val paintText: Paint = Paint()

    val hot = 9045099490000

    init {
        paintBandsFill.color = Color.parseColor("#33FF2C00")
        paintBandsFill.style = Paint.Style.FILL

        bg.color = Color.parseColor("#052773")
        bg.style = Paint.Style.FILL

        paintText.color = Color.parseColor("#AAFFFFFF")
        paintText.style = Paint.Style.FILL
        paintText.textSize = 12f.px

        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                active.set(false)
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                active.set(true)
            }
        })
    }

    var min = Float.MAX_VALUE
    var max = Float.MIN_VALUE

    fun drawFFT(canvas: Canvas) {

        val fftW = width / history.toFloat()
        val bandWH = height / resolution.toFloat()

        var x: Float
        var y: Float
        var band: FloatArray? = null

        canvas.drawColor(Color.parseColor("#11254C"))
        for (i in 0..ffts.size - 1) {
            synchronized(ffts) {
                band = ffts.elementAt(i)
            }

            x = width - (fftW * i)

            for (j in 0..resolution - 1) {
                y = height - (bandWH * j)
                val mag = band?.get(j) ?: .0f
                val pow = mag.toInt()

                paintBandsFill.color = Color.rgb(pow, pow, pow)
                canvas.drawRect(x - fftW, y - bandWH, x, y, paintBandsFill)

                if(mag > max) {
                    max = mag
                    Log.d(TAG, "=== MAX: " + max.toString())
                }

                if(mag < min) {
                    min = mag
                    Log.d(TAG, "=== MIN: " + min.toString())
                }
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

            for (j in 0..fft.size / resolution - 1 step 2) {
                accum += (Math.pow(fft[i * j].toDouble(), 2.0) + Math.pow(fft[i * j + 1].toDouble(), 2.0)).toFloat() //magnitudes
            }

            accum /= resolution
            bands[i] = accum
        }

        synchronized(ffts) {
            ffts.addFirst(bands)

            while (ffts.size > history)
                ffts.removeLast()
        }

        if (active.get()) {
            val canvas = holder.lockCanvas()
            drawFFT(canvas)
            holder.unlockCanvasAndPost(canvas)
        }
    }
}
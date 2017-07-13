package com.paramsen.noise.sample.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import java.util.*

/**
 * @author Pär Amsen 06/2017
 */
class AudioView(context: Context, attrs: AttributeSet?) : SimpleSurface(context, attrs) {
    val sec = 10
    val hz = 44100
    val merge = 512
    val history = hz * sec / merge
    val audio: ArrayDeque<Float> = ArrayDeque()

    val paintAudio: Paint = Paint()
    val paintLines: Paint = Paint()
    val paintText: Paint = textPaint()
    val path: Path = Path()

    val bg = Color.parseColor("#39424F")

    init {
        paintAudio.color = Color.parseColor("#23E830")
        paintAudio.strokeWidth = 0f
        paintAudio.style = Paint.Style.STROKE
    }

    fun drawAudio(canvas: Canvas): Canvas {
        path.reset()

        synchronized(audio) {
            for ((i, sample) in audio.withIndex()) {
                if (i == 0) path.moveTo(width.toFloat(), sample)
                path.lineTo(width - width * i / history.toFloat(), Math.min(sample * 0.175f + height / 2f, height.toFloat()))
            }
            if (audio.size in 1..(history - 1)) path.lineTo(0f, height / 2f)
        }

        canvas.drawColor(bg)
        canvas.drawPath(path, paintAudio)
        canvas.drawText("AUDIO", 16f.px, 24f.px, paintText)

        return canvas
    }

    fun onWindow(window: FloatArray) {
        synchronized(audio) {
            var accum = 0f

            for ((i, sample) in window.withIndex()) {
                if (i > 0 && i % merge != 0) {
                    accum += sample
                } else {
                    audio.addFirst(accum / merge)
                    accum = 0f
                }
            }

            while (audio.size > history)
                audio.removeLast()
        }

        drawSurface(this::drawAudio)
    }
}
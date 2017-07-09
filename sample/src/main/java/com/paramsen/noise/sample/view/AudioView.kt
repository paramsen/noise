package com.paramsen.noise.sample.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.SurfaceView
import android.view.View
import java.util.*

/**
 * @author Pär Amsen 06/2017
 */
class AudioView(context: Context, attrs: AttributeSet?) : SimpleSurface(context, attrs) {
    val sec = 10
    val hz = 44100
    val skip = 128
    val history = hz * sec / skip
    val audio: ArrayDeque<Float> = ArrayDeque()

    val paintAudio: Paint = Paint()
    val paintText: Paint = Paint()
    val path: Path = Path()

    init {
        paintAudio.color = Color.GREEN
        paintAudio.strokeWidth = 0f
        paintAudio.style = Paint.Style.STROKE

        paintText.color = Color.parseColor("#AAFFFFFF")
        paintText.style = Paint.Style.FILL
        paintText.textSize = 12f.px
    }

    fun drawAudio(canvas: Canvas): Canvas {
        path.reset()

        synchronized(audio) {
            for ((i, sample) in audio.withIndex()) {
                if (i == 0)
                    path.moveTo(width.toFloat(), sample)
                path.lineTo(width - width * i / history.toFloat(), sample * 0.175f + height / 2)
            }
            if (audio.size > 0 && audio.size < history)
                path.lineTo(0f, height / 2.toFloat())
        }

        canvas.drawColor(Color.GRAY)
        canvas.drawPath(path, paintAudio)
        canvas.drawText("AUDIO", 16f.px, 24f.px, paintText)

        return canvas
    }

    fun onWindow(window: FloatArray) {
        synchronized(audio) {
            var accum = 0f

            for ((i, sample) in window.withIndex()) {
                if (i > 0 && i % skip != 0)
                    accum += sample
                else {
                    audio.addFirst(accum / skip)
                    accum = 0f
                }
            }

            while (audio.size > history)
                audio.removeLast()
        }

        drawSurface(this::drawAudio)
    }
}
package com.paramsen.noise.sample.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import java.util.*

/**
 * @author Pär Amsen 06/2017
 */
class AudioView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    val sec = 10
    val hz = 44100
    val skip = 500
    val history = hz * sec / skip
    val audio: Deque<Float>

    val paint: Paint
    val path: Path

    init {
        audio = ArrayDeque()
        paint = Paint()
        paint.color = Color.GREEN
        paint.strokeWidth = 1f
        paint.style = Paint.Style.STROKE
        path = Path()
    }

    override fun onDraw(canvas: Canvas) {
        path.reset()

        synchronized(audio) {
            for ((i, sample) in audio.withIndex()) {
                if(i == 0)
                    path.moveTo(width.toFloat(), sample)
                path.lineTo(width - width * i / history.toFloat(), sample * 0.1f + height / 2)
            }
        }

        canvas.drawColor(Color.GRAY)
        canvas.drawPath(path, paint)
    }

    fun onWindow(window: FloatArray) {
        synchronized(audio) {
            window.forEachIndexed { i, sample ->
                if (i % skip == 0)
                    audio.addFirst(sample)
            }

            while (audio.size > history)
                audio.removeLast()

            postInvalidate()
        }
    }
}
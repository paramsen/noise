package com.paramsen.noise.sample.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Pär Amsen 07/2017
 */

abstract class SimpleSurface(context: Context, attrs: AttributeSet?) : SurfaceView(context, attrs) {
    val active = AtomicBoolean(false)

    init {
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

    fun drawSurface(ifActive: (Canvas) -> Canvas) {
        if (active.get()) {
            holder.unlockCanvasAndPost(ifActive(holder.lockCanvas()))
        }
    }
}
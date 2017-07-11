package com.paramsen.noise.sample.view

import android.content.Context
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Pär Amsen 07/2017
 */

class TipView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private val started = AtomicBoolean(false)

    fun schedule() {
        animIn()
    }

    private fun animIn() {
        if (started.compareAndSet(false, true)) {
            postDelayed({
                alpha = 0f
                visibility = View.VISIBLE
                animate().alpha(1f).setDuration(300).setInterpolator(LinearOutSlowInInterpolator()).onEnd { animOut() }
            }, 3000)
        }
    }

    private fun animOut() {
        postDelayed({
            alpha = 1f
            visibility = View.VISIBLE
            animate().alpha(0f).setDuration(300).setInterpolator(LinearOutSlowInInterpolator()).onEnd { visibility = GONE }
        }, 20000)
    }
}
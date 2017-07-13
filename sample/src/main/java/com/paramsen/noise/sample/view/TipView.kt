package com.paramsen.noise.sample.view

import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.paramsen.noise.sample.R
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author Pär Amsen 07/2017
 */

class TipView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    private val showed = AtomicBoolean(false)

    init {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        LayoutInflater.from(context).inflate(R.layout.view_tip, this, true)

        gravity = Gravity.CENTER_VERTICAL
        orientation = HORIZONTAL
        padding(8f.px.toInt())
        background = ContextCompat.getDrawable(context, R.drawable.tip_bg)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) elevation = 16f.px
    }

    fun schedule() {
        animIn()
    }

    private fun animIn() {
        if (showed.compareAndSet(false, true)) {
            postDelayed({
                alpha = 0f
                visibility = View.VISIBLE
                requestLayout()
                animate().alpha(1f).setDuration(300).setInterpolator(LinearOutSlowInInterpolator()).onTerminate { animOut() }
            }, 3000)
        }
    }

    private fun animOut() {
        postDelayed({
            alpha = 1f
            visibility = View.VISIBLE
            animate().alpha(0f).setDuration(300).setInterpolator(LinearOutSlowInInterpolator()).onTerminate { visibility = GONE }
        }, 20000)
    }
}
package com.paramsen.noise.sample.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import com.paramsen.noise.sample.BuildConfig
import com.paramsen.noise.sample.R
import kotlinx.android.synthetic.main.view_info.view.*

/**
 * @author Pär Amsen 07/2017
 */

class InfoView(context: Context?, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_info, this, true)

        background = ContextCompat.getDrawable(context, R.color.colorPrimary)
        padding(16f.px.toInt())

        version.text = "v${BuildConfig.VERSION_NAME}"
        github.setOnClickListener { browser("https://github.com/paramsen/noise") }
        me.setOnClickListener { browser("https://paramsen.github.io") }
        close.setOnClickListener { onClose() }
    }

    fun onShow() {
        if(visibility == View.VISIBLE)
            return onClose()

        clearAnimation()

        visibility = View.VISIBLE
        if(alpha != 1.0f) alpha = 1f

        ViewAnimationUtils.createCircularReveal(this, width - (12f + 8f).px.toInt(), 0, width / 20f, Math.hypot(width.toDouble(), height.toDouble()).toFloat())
                .setDuration(300)
                .start()
    }

    fun onClose() {
        clearAnimation()

        val oldY = y

        animate().alpha(0f)
                .yBy(-20f.px)
                .setDuration(200)
                .setInterpolator(AccelerateInterpolator())
                .onTerminate {
                    visibility = View.INVISIBLE
                    alpha = 1f
                    y = oldY
                }.start()
    }

    private fun browser(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}
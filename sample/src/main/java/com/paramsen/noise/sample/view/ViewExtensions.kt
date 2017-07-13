package com.paramsen.noise.sample.view

import android.animation.Animator
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.support.v7.app.ActionBar
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.Animation

/**
 * @author Pär Amsen 06/2017
 */

val Float.dp: Float
    get() = (this / Resources.getSystem().displayMetrics.density)
val Float.px: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

val textPaint: () -> Paint = {
    Paint().apply {
        color = Color.parseColor("#AAFFFFFF")
        style = Paint.Style.FILL
        textSize = 12f.px
        typeface = Typeface.MONOSPACE
    }
}

val errTextPaint: () -> Paint = {
    Paint().apply {
        color = Color.parseColor("#BBFF0000")
        style = Paint.Style.FILL
        textSize = 12f.px
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    }
}

fun ViewPropertyAnimator.onEnd(then: () -> Unit): ViewPropertyAnimator {
    this.setListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            then()
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }
    })

    return this
}

fun ViewPropertyAnimator.onTerminate(then: () -> Unit): ViewPropertyAnimator {
    this.setListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            then()
        }

        override fun onAnimationCancel(animation: Animator?) {
            then()
        }

        override fun onAnimationStart(animation: Animator?) {
        }
    })

    return this
}

fun Animation.onTerminate(then: () -> Unit): Animation {
    this.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            then()
        }

        override fun onAnimationStart(animation: Animation?) {
        }
    })

    return this
}

fun View.padding(i: Int) {
    setPadding(i, i, i, i)
}

fun ActionBar.view(): ViewGroup = customView.parent.parent as ViewGroup
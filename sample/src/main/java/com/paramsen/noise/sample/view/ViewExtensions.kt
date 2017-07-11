package com.paramsen.noise.sample.view

import android.animation.Animator
import android.content.res.Resources
import android.view.ViewPropertyAnimator

/**
 * @author Pär Amsen 06/2017
 */

val Float.dp: Float
    get() = (this / Resources.getSystem().displayMetrics.density)
val Float.px: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

fun ViewPropertyAnimator.onEnd(then: () -> Unit) {
    this.setListener(object: Animator.AnimatorListener {
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
}
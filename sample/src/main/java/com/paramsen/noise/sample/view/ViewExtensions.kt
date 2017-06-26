package com.paramsen.noise.sample.view

import android.content.res.Resources

/**
 * @author Pär Amsen 06/2017
 */

val Float.dp: Float
    get() = (this / Resources.getSystem().displayMetrics.density)
val Float.px: Float
    get() = (this * Resources.getSystem().displayMetrics.density)
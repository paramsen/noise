package com.paramsen.noise.sample.view

import android.graphics.Color
import android.util.Log

/**
 * @author Pär Amsen 07/2017
 */

object Spectogram {
    val TAG = javaClass.simpleName!!

    private val a = intArrayOf(59, 59, 59)
    private val b = intArrayOf(28, 135, 255)
    private val c = intArrayOf(254, 73, 74)
    private val d = intArrayOf(249, 255, 23)
    private val e = intArrayOf(255, 255, 250)

    private val range = 256
    private val spectogram: IntArray = generate()

    fun color(f: Double): Int {
        return spectogram[Math.min(((256 - 1) * f), range.toDouble() - 1).toInt()]
    }

    private fun generate(): IntArray {
        Log.d(TAG, "generate spectogram colors")

        val spectogram = IntArray(range)

        for (i in 0..range - 1) {
            val f = i / range.toDouble()
            val f1 = f * 4.0 % 1.0

            val blend = when ((f * 100).toInt()) {
                in 0..24 -> Pair(a, b)
                in 25..49 -> Pair(b, c)
                in 50..74 -> Pair(c, d)
                else -> Pair(d, e)
            }

            spectogram[i] = rgb(blend.first
                    .map { it * (1.0 - f1) }
                    .zip(blend.second.map { it * f1 })
                    .map { ((it.first + it.second) / 2).toInt() })
        }

        return spectogram
    }

    private fun rgb(rgb: List<Int>): Int {
        return Color.rgb(rgb[0], rgb[1], rgb[2])
    }
}
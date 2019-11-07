package com.paramsen.noise.sample.view

import android.util.Log

class Profiler(val tag: String) {
    val TAG = javaClass.simpleName

    private var count = 0L
    private var time = 0L

    private val hashes = HashMap<Int, Int>()

    fun next() {
        if (time == 0L) time = System.currentTimeMillis()

        if (System.currentTimeMillis() - time > 1000L) {
            time = System.currentTimeMillis()
            Log.d(TAG, "===$tag: $count/1000ms")
            count = 0
        } else {
            ++count
        }
    }

    fun next(hash: Int) {
        if (time == 0L) time = System.currentTimeMillis()

        hashes[hash] = (hashes[hash] ?: 0).inc()

        if (System.currentTimeMillis() - time > 1000L) {
            time = System.currentTimeMillis()
            hashes.forEach { if (it.value > 1) Log.d(TAG, "===$tag: ${it.value}") }
            Log.d(TAG, "===$tag: $count/1000ms")
            count = 0
            hashes.clear()
        } else {
            ++count
        }
    }
}
package com.paramsen.noise

import java.io.Closeable

/**
 * Build instance -> Use instance to compute FFTs -> Close instance to free native allocations
 *
 * @author Pär Amsen 11/2019
 */
class Noise(private val configPointer: Long, private val isReal: Boolean) : Closeable {
    /** @return dst */
    fun fft(src: FloatArray, dst: FloatArray): FloatArray {
        if (isReal) {
            NoiseNativeBridge.real(src, dst, configPointer)
        } else {
            NoiseNativeBridge.imaginary(src, dst, configPointer)
        }

        return dst
    }

    override fun close() {
        if (isReal) {
            NoiseNativeBridge.realConfigDispose(configPointer)
        } else {
            NoiseNativeBridge.imaginaryConfigDispose(configPointer)
        }
    }

    companion object {
        @JvmStatic
        fun real(n: Int): Noise {
            return Noise(NoiseNativeBridge.realConfig(n), true)
        }

        @JvmStatic
        fun imaginary(n: Int): Noise {
            return Noise(NoiseNativeBridge.imaginaryConfig(n), false)
        }
    }
}
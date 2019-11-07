package com.paramsen.noise

import java.io.Closeable

/**
 * Instances should be closed when no longer in use to free native allocations.
 *
 * @author Pär Amsen 11/2019
 */
class Noise private constructor(private val configPointer: Long, private val isReal: Boolean) : Closeable {
    /** @return dst for convenience */
    fun fft(src: FloatArray, dst: FloatArray): FloatArray {
        if (isReal) {
            require(src.size == dst.size + 2) { "Cannot compute FFT, dst length must equal src length + 2" }
            NoiseNativeBridge.real(src, dst, configPointer)
        } else {
            require(src.size == dst.size) { "Cannot compute FFT, dst length must equal src length" }
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
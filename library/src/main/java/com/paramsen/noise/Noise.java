package com.paramsen.noise;

/**
 * Consult README.md for usage and samples.
 *
 * @author Pär Amsen 06/2017
 */
public class Noise {

    /**
     * @return Instance that performs FFT on real data. Consult readme for info.
     */
    public static ImplBuilder real() {
        return new ImplBuilder(true);
    }

    /**
     * @return Instance that performs FFT on imaginary data. Consult readme for info.
     */
    public static ImplBuilder imaginary() {
        return new ImplBuilder(false);
    }

    public static class ImplBuilder {
        private boolean real;

        private ImplBuilder(boolean real) {
            this.real = real;
        }

        /**
         * @return Instance that can be called concurrently with varying data sizes.
         */
        public NoiseThreadSafe threadSafe() {
            if (real) return new NoiseThreadSafe(NoiseNativeBridge::realThreadSafe);
            else return new NoiseThreadSafe(NoiseNativeBridge::imaginaryThreadSafe);
        }

        /**
         * Performs computations nearly 2x faster than threadSafe(), but must be called sequentially
         * with a fixed data size. Initialize by calling init(int, bool) before calling fft(float[])
         */
        public NoiseOptimized optimized() {
            if (real)
                return new NoiseOptimized(NoiseNativeBridge::realOptimized, NoiseNativeBridge::realOptimizedCfg, inSize -> new float[inSize + 2], NoiseNativeBridge::realOptimizedCfgDispose);
            else
                return new NoiseOptimized(NoiseNativeBridge::imaginaryOptimized, NoiseNativeBridge::imaginaryOptimizedCfg, float[]::new, NoiseNativeBridge::imaginaryOptimizedCfgDispose);
        }
    }
}

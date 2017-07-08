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
            if (real) return new NoiseThreadSafe(new Func2() {
                @Override
                public void fft(float[] in, float[] out) {
                    NoiseNativeBridge.realThreadSafe(in, out);
                }
            });
            else return new NoiseThreadSafe(new Func2() {
                @Override
                public void fft(float[] in, float[] out) {
                    NoiseNativeBridge.imaginaryThreadSafe(in, out);
                }
            });
        }

        /**
         * Performs computations nearly 2x faster than threadSafe(), but must be called sequentially
         * with a fixed data size. Initialize by calling init(int, bool) before calling fft(float[])
         */
        public NoiseOptimized optimized() {
            if (real)
                return new NoiseOptimized(new Func3() {
                    @Override
                    public void fft(float[] in, float[] out, long cfgPointer) {
                        NoiseNativeBridge.realOptimized(in, out, cfgPointer);
                    }
                }, new FuncO1<Long, Integer>() {
                    @Override
                    public Long call(Integer inSize1) {
                        return NoiseNativeBridge.realOptimizedCfg(inSize1);
                    }
                }, new FuncO1<float[], Integer>() {
                    @Override
                    public float[] call(Integer inSize) {
                        return new float[inSize + 2];
                    }
                }, new Func1<Long>() {
                    @Override
                    public void call(Long cfgPointer) {
                        NoiseNativeBridge.realOptimizedCfgDispose(cfgPointer);
                    }
                });
            else
                return new NoiseOptimized(new Func3() {
                    @Override
                    public void fft(float[] in, float[] out, long cfgPointer) {
                        NoiseNativeBridge.imaginaryOptimized(in, out, cfgPointer);
                    }
                }, new FuncO1<Long, Integer>() {
                    @Override
                    public Long call(Integer inSize) {
                        return NoiseNativeBridge.imaginaryOptimizedCfg(inSize);
                    }
                }, new FuncO1<float[], Integer>() {
                    @Override
                    public float[] call(Integer in) {
                        return new float[in];
                    }
                }, new Func1<Long>() {
                    @Override
                    public void call(Long cfgPointer) {
                        NoiseNativeBridge.imaginaryOptimizedCfgDispose(cfgPointer);
                    }
                });
        }
    }
}

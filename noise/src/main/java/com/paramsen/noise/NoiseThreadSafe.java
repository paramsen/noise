package com.paramsen.noise;

/**
 * Allocates and release memory during each call to fft(float[] in, float[] out). This class is thus
 * threadsafe and can be used concurrently, at the cost of near 2x execution time in comparison to
 * the optimized version (NoiseOptimized).
 *
 * @author Pär Amsen 06/2017
 */
public class NoiseThreadSafe {
    private Func2 fft;
    private FuncO1<float[], Integer> outFactory;

    public NoiseThreadSafe(Func2 fft, FuncO1<float[], Integer> outFactory) {
        this.fft = fft;
        this.outFactory = outFactory;
    }

    /**
     * @param in data to be processed
     * @param out result of computation
     * @return float[] of length:
     *      Real: in.length + 2
     *      Imaginary: in.length
     */
    public float[] fft(float[] in, float[] out) {
        fft.fft(in, out);
        return out;
    }

    /**
     * Allocates a new float[] to store result in which is returned for each invocation
     *
     * @param in data to be processed
     * @return float[] out
     */
    public float[] fft(float[] in) {
        float[] out = outFactory.call(in.length);
        fft.fft(in, out);
        return out;
    }
}

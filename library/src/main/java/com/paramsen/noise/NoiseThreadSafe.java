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

    public NoiseThreadSafe(Func2 fft) {
        this.fft = fft;
    }

    /**
     * @param in data to be processed
     * @param out result of computation
     * @return float[] out
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
        float[] out = new float[in.length + 2];
        fft.fft(in, out);
        return out;
    }
}

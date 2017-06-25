package com.paramsen.noise;

/**
 * @author Pär Amsen 06/2017
 */
public class NoiseThreadSafe {
    private Func2 fft;

    public NoiseThreadSafe(Func2 fft) {
        this.fft = fft;
    }

    public void fft(float[] in, float[] out) {
        fft.fft(in, out);
    }
}

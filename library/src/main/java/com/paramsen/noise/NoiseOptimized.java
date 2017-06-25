package com.paramsen.noise;

/**
 * @author Pär Amsen 06/2017
 */
public class NoiseOptimized {
    private long cfgPointer;
    private Func3 fft;

    public NoiseOptimized(Func3 fft) {
        this.fft = fft;
    }

    public NoiseOptimized init(int inSize) {
        cfgPointer = NoiseNativeBridge.nRealOptimizedCfg(inSize);
        return this;
    }

    public void fft(float[] in, float[] out) {
        fft.fft(in, out, cfgPointer);
    }

    public void dispose() {
        NoiseNativeBridge.nRealOptimizedCfgDispose(cfgPointer);
    }
}

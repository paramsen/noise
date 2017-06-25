package com.paramsen.noise;

/**
 * @author Pär Amsen 06/2017
 */
public class NoiseOptimized {
    private int inSize;
    private long cfgPointer;
    private float[] out;

    private Func3 fft;
    private final Func1 outFactory;

    public NoiseOptimized(Func3 fft, Func1 outFactory) {
        this.fft = fft;
        this.outFactory = outFactory;
    }

    public NoiseOptimized init(int inSize, boolean internalStorage) {
        this.inSize = inSize;
        this.cfgPointer = NoiseNativeBridge.realOptimizedCfg(inSize);

        if(internalStorage)
            out = outFactory.create(inSize);

        return this;
    }

    public float[] fft(float[] in, float[] out) {
        fft.fft(in, out, cfgPointer);
        return out;
    }

    public float[] fft(float[] in) {
        if(out == null)
            throw new RuntimeException("Enable in initialization to use internal storage");

        fft.fft(in, out, cfgPointer);

        return out;
    }

    public void dispose() {
        NoiseNativeBridge.realOptimizedCfgDispose(cfgPointer);
    }
}

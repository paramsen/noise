package com.paramsen.noise;

/**
 * @author Pär Amsen 06/2017
 */
public class NoiseOptimized {
    private int inSize;
    private long cfgPointer;
    private float[] out;

    private Func3 fft;
    private Func1<Long, Integer> cfgFactory;
    private Func1<float[], Integer> outFactory;

    public NoiseOptimized(Func3 fft, Func1<Long, Integer> cfgFactory, Func1<float[], Integer> outFactory) {
        this.fft = fft;
        this.cfgFactory = cfgFactory;
        this.outFactory = outFactory;
    }

    public NoiseOptimized init(int inSize, boolean internalStorage) {
        this.inSize = inSize;
        this.cfgPointer = cfgFactory.call(inSize);

        if(internalStorage)
            out = outFactory.call(inSize);

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

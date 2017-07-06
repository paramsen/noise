package com.paramsen.noise;

/**
 * Pre allocates memory in native C (JNI) to use for computations. Since there's shared buffers, an
 * instance of this class cannot be used concurrently.
 *
 * Memory is allocated lazily when init(..) is called.
 *
 * This implementation is roughly 2x faster than NoiseThreadSafe.
 *
 * @author Pär Amsen 06/2017
 */
public class NoiseOptimized {
    private int inSize;
    private long cfgPointer;
    private float[] out;

    private Func3 fft;
    private FuncO1<Long, Integer> cfgFactory;
    private FuncO1<float[], Integer> outFactory;
    private Func1<Long> dispose;

    public NoiseOptimized(Func3 fft, FuncO1<Long, Integer> cfgFactory, FuncO1<float[], Integer> outFactory, Func1<Long> dispose) {
        this.fft = fft;
        this.cfgFactory = cfgFactory;
        this.outFactory = outFactory;
        this.dispose = dispose;
    }

    /**
     * @param inSize Predefined size of float[] that fft(..) will be called with.
     *
     *               For real data the input data length must be an even number due to limitations
     *               of FFT implementation.
     *
     *               For imaginary data the pairs are stored like float[0] = r0, float[1] = i0,
     *               float[2] = r1, float[3] = i1 thus float[even] = next real, float[odd] = next
     *               imaginary.
     *
     * @param internalStorage if true, an internal float[] will be initialized which calls to
     *                        fft(float[]) will return with the computation result.
     */
    public NoiseOptimized init(int inSize, boolean internalStorage) {
        this.inSize = inSize;
        this.cfgPointer = cfgFactory.call(inSize);

        if(internalStorage)
            out = outFactory.call(inSize);

        return this;
    }

    /**
     * Use if init was called with internalStorage == false.
     *
     * @param in data to be processed
     * @param out result of computation
     * @return float[] out
     */
    public float[] fft(float[] in, float[] out) {
        fft.fft(in, out, cfgPointer);
        return out;
    }

    /**
     * Use if init was called with internalStorage == true.
     *
     * @param in data to be processed
     * @return float[] result
     */
    public float[] fft(float[] in) {
        if(out == null)
            throw new RuntimeException("Enable internalStorage in initialization to use internal storage");

        fft.fft(in, out, cfgPointer);

        return out;
    }

    /**
     * Dispose of allocated memory by native C code.
     */
    public void dispose() {
        dispose.call(cfgPointer);
    }
}

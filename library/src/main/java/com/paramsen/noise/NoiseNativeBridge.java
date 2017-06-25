package com.paramsen.noise;

/**
 * @author Pär Amsen 06/2017
 */
public class NoiseNativeBridge {
    static {
        System.loadLibrary("noise");
    }

    public static void real(float[] in, float[] out) {
        nRealThreadSafe(in, out);
    }

    public static void imaginary(float[] in, float[] out) {
        nImaginaryThreadSafe(in, out);
    }

    static native void nRealThreadSafe(float[] in, float[] out);
    static native void nImaginaryThreadSafe(float[] in, float[] out);

    public static native long nRealOptimizedCfg(int inSize);
    public static native void nRealOptimized(float[] in, float[] out, long cfgPointer);
    public static native long nRealOptimizedCfgDispose(long cfgPointer);
}

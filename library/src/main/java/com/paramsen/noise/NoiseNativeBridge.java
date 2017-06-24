package com.paramsen.noise;

/**
 * @author Pär Amsen 06/2017
 */
public class NoiseNativeBridge {
    static {
        System.loadLibrary("noise");
    }

    public static void real(float[] in, float[] out) {
        nReal(in, out);
    }

    public static void imaginary(float[] in, float[] out) {
        nImaginary(in, out);
    }

    static native void nReal(float[] in, float[] out);
    static native void nImaginary(float[] in, float[] out);
    public static native void nRealOpt(float[] in, float[] out);
}

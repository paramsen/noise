package com.paramsen.noise;

/**
 * JNI interface
 *
 * @author Pär Amsen 06/2017
 */
public class NoiseNativeBridge {
    static {
        System.loadLibrary("noise");
    }

    public static native void realThreadSafe(float[] in, float[] out);
    public static native long realOptimizedCfg(int inSize);
    public static native void realOptimized(float[] in, float[] out, long cfgPointer);
    public static native long realOptimizedCfgDispose(long cfgPointer);

    public static native void imaginaryThreadSafe(float[] in, float[] out);
    public static native long imaginaryOptimizedCfg(int inSize);
    public static native void imaginaryOptimized(float[] in, float[] out, long cfgPointer);
    public static native long imaginaryOptimizedCfgDispose(long cfgPointer);
}

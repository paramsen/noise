package com.paramsen.noise;

/**
 * @author Pär Amsen 06/2017
 */
public class Noise {
    public static void real(float[] in, float[] out) {
        NoiseNativeBridge.real(in, out);
    }

    public static void imaginary(float[] in, float[] out) {
        NoiseNativeBridge.imaginary(in, out);
    }

}

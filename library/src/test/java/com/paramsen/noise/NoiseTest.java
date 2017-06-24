package com.paramsen.noise;

import org.junit.Test;

/**
 * @author Pär Amsen 06/2017
 */
public class NoiseTest {
    @Test
    public void use() {
        //Noise.real().optimized().fft([]) TODO OPTIMIZED (shared alloc buff) FFT
        //Noise.real().optimized().buffer([]) then noise.fft() TODO DIRECT INTERNAL BUFFER USE
        //Noise.real().threadsafe().fft([]) //TODO NEW BUFF

        //Noise.imaginary().optimized().fft([]) TODO OPTIMIZED (shared alloc buff) FFT
        //Noise.imaginary().optimized().buffer([]) then noise.fft() TODO DIRECT INTERNAL BUFFER USE
        //Noise.imaginary().threadsafe().fft([]) //TODO NEW BUFF

        Noise.real(new float[4096]);
    }
}
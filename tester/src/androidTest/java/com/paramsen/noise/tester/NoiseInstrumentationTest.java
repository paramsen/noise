package com.paramsen.noise.tester;

import android.support.test.runner.AndroidJUnit4;

import com.paramsen.noise.Noise;
import com.paramsen.noise.NoiseNativeBridge;
import com.paramsen.noise.NoiseOptimized;
import com.paramsen.noise.NoiseThreadSafe;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NoiseInstrumentationTest {
    @Test
    public void testRealThreadSafe_Profile() throws Exception {
        int runs = 0;
        NoiseThreadSafe noise = Noise.real().threadSafe();
        long start = System.currentTimeMillis();

        while(System.currentTimeMillis() - start <= 1000) {
            noise.fft(new float[4096], new float[4096 + 2]);
            runs++;
        }
        System.out.println("============");
        System.out.printf("=== RTS: %d/1000ms\n", runs);
        System.out.println("============");
    }

    @Test
    public void testRealOptimized_Profile() throws Exception {
        int runs = 0;
        NoiseOptimized noise = Noise.real().optimized().init(4096);
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis() - start <= 1000) {
            noise.fft(new float[4096], new float[4096 + 2]);
            runs++;
        }
        noise.dispose();

        System.out.println("============");
        System.out.printf("=== ROS: %d/1000ms\n", runs);
        System.out.println("============");
    }
}

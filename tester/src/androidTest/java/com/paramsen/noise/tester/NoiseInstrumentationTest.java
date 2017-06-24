package com.paramsen.noise.tester;

import android.support.test.runner.AndroidJUnit4;

import com.paramsen.noise.Noise;
import com.paramsen.noise.NoiseNativeBridge;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NoiseInstrumentationTest {
    @Test
    public void testA() {

    }

    @Test
    public void testB() throws Exception {
        int i = 0;
        long start = System.currentTimeMillis();

        while(System.currentTimeMillis() - start <= 100) {
            Noise.real(new float[4096], new float[4096 + 2]);
            i++;
        }
        Assert.assertEquals(i, Integer.MAX_VALUE);
        //Noise.imaginary(new float[4096 * 2], new float[4096 * 2]);
    }

    @Test
    public void testC() throws Exception {
        int i = 0;
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis() - start <= 100) {
            NoiseNativeBridge.nRealOpt(new float[4096], new float[4096 + 2]);
            i++;
        }
        Assert.assertEquals(i, Integer.MAX_VALUE);
    }
}

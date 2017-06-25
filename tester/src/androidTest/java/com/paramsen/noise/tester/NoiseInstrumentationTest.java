package com.paramsen.noise.tester;

import android.support.test.runner.AndroidJUnit4;

import com.paramsen.noise.Noise;
import com.paramsen.noise.NoiseNativeBridge;
import com.paramsen.noise.NoiseOptimized;
import com.paramsen.noise.NoiseThreadSafe;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

@RunWith(AndroidJUnit4.class)
public class NoiseInstrumentationTest {
    @Test
    public void testRealThreadSafe_Profile() throws Exception {
        NoiseThreadSafe noise = Noise.real().threadSafe();
        long runs = loopFor(1, TimeUnit.SECONDS, () -> noise.fft(new float[4096], new float[4096 + 2]));

        System.out.println("============");
        System.out.printf("=== RTS: %d/1000ms\n", runs);
        System.out.println("============");
    }

    //75% faster (emulator)
    @Test
    public void testRealOptimized_Profile() throws Exception {
        NoiseOptimized noise = Noise.real().optimized().init(4096);
        long runs = loopFor(1, TimeUnit.SECONDS, () -> noise.fft(new float[4096], new float[4096 + 2]));
        noise.dispose();

        System.out.println("============");
        System.out.printf("=== ROS: %d/1000ms\n", runs);
        System.out.println("============");
    }

    @Test
    public void testImaginaryThreadSafe_Profile() throws Exception {
        NoiseThreadSafe noise = Noise.imaginary().threadSafe();
        long runs = loopFor(1, TimeUnit.SECONDS, () -> noise.fft(new float[4096], new float[4096]));

        System.out.println("============");
        System.out.printf("=== ITS: %d/1000ms\n", runs);
        System.out.println("============");
    }

    //30% faster (emulator)
    @Test
    public void testImaginaryOptimized_Profile() throws Exception {
        NoiseOptimized noise = Noise.imaginary().optimized().init(4096);
        long runs = loopFor(1, TimeUnit.SECONDS, () -> noise.fft(new float[4096], new float[4096]));
        noise.dispose();

        System.out.println("============");
        System.out.printf("=== IOS: %d/1000ms\n", runs);
        System.out.println("============");
    }

    /**
     * Profile how many times Runnable *each* can execute during long *time* of TimeUnit *unit*
     */
    private long loopFor(long time, TimeUnit unit, Runnable each) {
        long start = System.currentTimeMillis();
        int runs = 0;
        while(System.currentTimeMillis() - start <= unit.toMillis(time)) {
            each.run();
            runs++;
        }
        return runs;
    }
}

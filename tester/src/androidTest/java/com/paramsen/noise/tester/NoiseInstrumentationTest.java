package com.paramsen.noise.tester;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.paramsen.noise.Noise;
import com.paramsen.noise.NoiseOptimized;
import com.paramsen.noise.NoiseThreadSafe;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static junit.framework.Assert.assertEquals;

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

    @Test
    public void testRealOptimized_Profile() throws Exception {
        NoiseOptimized noise = Noise.real().optimized().init(4096, false);
        long runs = loopFor(1, TimeUnit.SECONDS, () -> noise.fft(new float[4096], new float[4096 + 2]));
        noise.dispose();

        System.out.println("============");
        System.out.printf("=== ROS: %d/1000ms\n", runs);
        System.out.println("============");
    }

    @Test
    public void testRealOptimized_internalStorage() throws Exception {
        NoiseOptimized noise = Noise.real().optimized().init(4096, true);
        loopFor(100, TimeUnit.MILLISECONDS, () -> noise.fft(new float[4096]));
        noise.dispose();
    }

    @Test
    public void testRealOptimized_Prove() throws Exception {
        NoiseOptimized noise = Noise.real().optimized().init(4096, true);,
        float[] input = new FloatsSource(InstrumentationRegistry.getTargetContext().getAssets().open("test/sample_signal_4096.dat")).get();
        float[] result = new FloatsSource(InstrumentationRegistry.getTargetContext().getAssets().open("test/sample_signal_4096_result.dat")).get();
        loopFor(100, TimeUnit.MILLISECONDS, () -> {
            float[] fft = noise.fft(input);
            
            for (int i = 0; i < input.length; i++) {
                assertEquals(result[i], fft[i]);
            }
        });
        noise.dispose();
    }

    @Test
    public void testImaginaryThreadSafe_Profile() throws Exception {
        NoiseThreadSafe noise = Noise.imaginary().threadSafe();
        long runs = loopFor(1, TimeUnit.SECONDS, () -> noise.fft(new float[4096], new float[4096]));

        System.out.println("============");
        System.out.printf("=== ITS: %d/1000ms\n", runs);
        System.out.println("============");
    }

    @Test
    public void testImaginaryOptimized_Profile() throws Exception {
        NoiseOptimized noise = Noise.imaginary().optimized().init(4096, false);
        long runs = loopFor(1, TimeUnit.SECONDS, () -> noise.fft(new float[4096], new float[4096]));
        noise.dispose();

        System.out.println("============");
        System.out.printf("=== IOS: %d/1000ms\n", runs);
        System.out.println("============");
    }

    @Test
    public void testImaginaryOptimized_internalStorage() throws Exception {
        NoiseOptimized noise = Noise.imaginary().optimized().init(4096, true);
        loopFor(100, TimeUnit.MILLISECONDS, () -> noise.fft(new float[4096]));
        noise.dispose();
    }

    /**
     * Run Runnable *each* repeatedly during long *time* of TimeUnit *unit* and return how many
     * times Runnable *each* was run. For profiling.
     */
    private long loopFor(long time, TimeUnit unit, Runnable each) {
        long start = System.currentTimeMillis();
        int runs = 0;
        while (System.currentTimeMillis() - start <= unit.toMillis(time)) {
            each.run();
            runs++;
        }
        return runs;
    }
}

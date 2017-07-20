package com.paramsen.noise.tester;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.paramsen.noise.Noise;
import com.paramsen.noise.NoiseOptimized;
import com.paramsen.noise.NoiseThreadSafe;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;

/**
 * Reminder: org.junit and android.support.test imports fail if Build Variant == release
 */
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

    /**
     * Assert that the output on a predefined signal of 4096 from kissfft is exactly the same for
     * this library as it is for kissfft. The "prerecorded" FFT is created through the
     * cpp_test_data_suite which simply compiles and runs kissfft on the input dataset in assets.
     * <p>
     * Hence, if this test is green kissfft works as intended.
     */
    @Test
    public void testRealThreadSafe_Assert_kissfft_compare_result() throws Exception {
        NoiseThreadSafe noise = Noise.real().threadSafe();
        float[] input = new FloatsSource(InstrumentationRegistry.getTargetContext().getAssets().open("test/sample_signal_4096.dat")).get();
        float[] kissfftPrerecordedFFT = new FloatsSource(InstrumentationRegistry.getTargetContext().getAssets().open("test/sample_signal_4096_result_real.dat")).get();

        loopFor(100, TimeUnit.MILLISECONDS, () -> {
            float[] fft = noise.fft(input);

            for (int i = 0; i < input.length; i++) {
                assertEquals(kissfftPrerecordedFFT[i], fft[i]);
            }
        });
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

    /**
     * Assert that the output on a predefined signal of 4096 from kissfft is exactly the same for
     * this library as it is for kissfft. The "prerecorded" FFT is created through the
     * cpp_test_data_suite which simply compiles and runs kissfft on the input dataset in assets.
     * <p>
     * Hence, if this test is green kissfft works as intended.
     */
    @Test
    public void testRealOptimized_Assert_kissfft_compare_result() throws Exception {
        NoiseOptimized noise = Noise.real().optimized().init(4096, true);
        float[] input = new FloatsSource(InstrumentationRegistry.getTargetContext().getAssets().open("test/sample_signal_4096.dat")).get();
        float[] kissfftPrerecordedFFT = new FloatsSource(InstrumentationRegistry.getTargetContext().getAssets().open("test/sample_signal_4096_result_real.dat")).get();

        loopFor(100, TimeUnit.MILLISECONDS, () -> {
            float[] fft = noise.fft(input);

            for (int i = 0; i < input.length; i++) {
                assertEquals(kissfftPrerecordedFFT[i], fft[i]);
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

    /**
     * Assert that the output on a predefined signal of 4096 from kissfft is exactly the same for
     * this library as it is for kissfft. The "prerecorded" FFT is created through the
     * cpp_test_data_suite which simply compiles and runs kissfft on the input dataset in assets.
     * <p>
     * Hence, if this test is green kissfft works as intended.
     */
    @Test
    public void testImaginaryThreadSafe_Assert_kissfft_compare_result() throws Exception {
        NoiseThreadSafe noise = Noise.imaginary().threadSafe();
        float[] inputFromFile = new FloatsSource(InstrumentationRegistry.getTargetContext().getAssets().open("test/sample_signal_4096.dat")).get();
        float[] input = new float[4096 * 2];
        float[] kissfftPrerecordedFFT = new FloatsSource(InstrumentationRegistry.getTargetContext().getAssets().open("test/sample_signal_4096_result_imag.dat")).get();

        for (int i = 0; i < inputFromFile.length; i++) {
            input[i * 2] = inputFromFile[i];
            input[i * 2 + 1] = inputFromFile[i];
        }

        loopFor(100, TimeUnit.MILLISECONDS, () -> {
            float[] fft = noise.fft(input);

            for (int i = 0; i < input.length; i++) {
                assertEquals(kissfftPrerecordedFFT[i], fft[i]);
            }
        });
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
     * Assert that the output on a predefined signal of 4096 from kissfft is exactly the same for
     * this library as it is for kissfft. The "prerecorded" FFT is created through the
     * cpp_test_data_suite which simply compiles and runs kissfft on the input dataset in assets.
     * <p>
     * Hence, if this test is green kissfft works as intended.
     */
    @Test
    public void testImaginaryOptimized_Assert_kissfft_compare_result() throws Exception {
        NoiseOptimized noise = Noise.imaginary().optimized().init(4096 * 2, true);
        float[] inputFromFile = new FloatsSource(InstrumentationRegistry.getTargetContext().getAssets().open("test/sample_signal_4096.dat")).get();
        float[] input = new float[4096 * 2];
        float[] kissfftPrerecordedFFT = new FloatsSource(InstrumentationRegistry.getTargetContext().getAssets().open("test/sample_signal_4096_result_imag.dat")).get();

        for (int i = 0; i < inputFromFile.length; i++) {
            input[i * 2] = inputFromFile[i];
            input[i * 2 + 1] = inputFromFile[i];
        }

        loopFor(100, TimeUnit.MILLISECONDS, () -> {
            float[] fft = noise.fft(input);

            for (int i = 0; i < input.length; i++) {
                assertEquals(kissfftPrerecordedFFT[i], fft[i]);
            }
        });

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

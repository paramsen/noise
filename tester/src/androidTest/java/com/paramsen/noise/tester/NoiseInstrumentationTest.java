package com.paramsen.noise.tester;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.paramsen.noise.Noise;

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
    public void testReal_Profile() throws Exception {
        Noise noise = Noise.real(4096);
        float[] src = new float[4096];
        float[] dst = new float[4096 + 2];
        long runs = loopFor(1, TimeUnit.SECONDS, () -> noise.fft(src, dst));
        noise.close();

        System.out.println("============");
        System.out.printf("=== ROS: %.2fms\n", ((float) runs) / 1000);
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
    public void testReal_Assert_kissfft_compare_result() throws Exception {
        Noise noise = Noise.real(4096);
        float[] input = new FloatsSource(InstrumentationRegistry.getTargetContext().getAssets().open("test/sample_signal_4096.dat")).get();
        float[] output = new float[input.length + 2];
        float[] kissfftPrerecordedFFT = new FloatsSource(InstrumentationRegistry.getTargetContext().getAssets().open("test/sample_signal_4096_result_real.dat")).get();

        loopFor(100, TimeUnit.MILLISECONDS, () -> {
            float[] fft = noise.fft(input, output);

            for (int i = 0; i < input.length; i++) {
                assertEquals(kissfftPrerecordedFFT[i], fft[i]);
            }
        });

        noise.close();
    }

    @Test
    public void testImaginary_Profile() throws Exception {
        Noise noise = Noise.imaginary(4096);
        float[] src = new float[4096];
        float[] dst = new float[4096];
        long runs = loopFor(1, TimeUnit.SECONDS, () -> noise.fft(src, dst));
        noise.close();

        System.out.println("============");
        System.out.printf("=== IOS: %.2fms\n", ((float) runs) / 1000);
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
    public void testImaginary_Assert_kissfft_compare_result() throws Exception {
        Noise noise = Noise.imaginary(4096 * 2);
        float[] inputFromFile = new FloatsSource(InstrumentationRegistry.getTargetContext().getAssets().open("test/sample_signal_4096.dat")).get();
        float[] input = new float[4096 * 2];
        float[] output = new float[4096 * 2];
        float[] kissfftPrerecordedFFT = new FloatsSource(InstrumentationRegistry.getTargetContext().getAssets().open("test/sample_signal_4096_result_imag.dat")).get();

        for (int i = 0; i < inputFromFile.length; i++) {
            input[i * 2] = inputFromFile[i];
            input[i * 2 + 1] = inputFromFile[i];
        }

        loopFor(100, TimeUnit.MILLISECONDS, () -> {
            float[] fft = noise.fft(input, output);

            for (int i = 0; i < input.length; i++) {
                assertEquals(kissfftPrerecordedFFT[i], fft[i]);
            }
        });

        noise.close();
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

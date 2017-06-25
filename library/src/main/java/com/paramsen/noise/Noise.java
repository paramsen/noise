package com.paramsen.noise;

/**
 * @author Pär Amsen 06/2017
 */
public class Noise {
    public static Builder real() {
        return new Builder(true);
    }

    public static Builder imaginary() {
        return new Builder(false);
    }

    public static class Builder {
        private boolean real;

        private Builder(boolean real) {
            this.real = real;
        }

        public NoiseThreadSafe threadSafe() {
            if (real) return new NoiseThreadSafe(NoiseNativeBridge::realThreadSafe);
            else return new NoiseThreadSafe(NoiseNativeBridge::imaginaryThreadSafe);
        }

        public NoiseOptimized optimized() {
            if (real)
                return new NoiseOptimized(NoiseNativeBridge::realOptimized);
            else
                return new NoiseOptimized((in, out, cfgPointer) -> {
                    NoiseNativeBridge.realThreadSafe(in, out);
                });
        }
    }
}

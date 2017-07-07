package com.paramsen.noise.tester;

import com.google.common.io.LittleEndianDataInputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * @author Pär Amsen 07/2017
 */
public class FloatsSource {
    private InputStream input;

    public FloatsSource(InputStream input) {
        this.input = input;
    }

    public float[] get() {
        try (LittleEndianDataInputStream dis = new LittleEndianDataInputStream(new BufferedInputStream(input))) {
            int size = dis.readInt();
            float[] floats = new float[size];

            float next = 0f;
            int read = 0;

            while (read < size) {
                next = nextFloat(dis);
                floats[read++] = next;
            }

            if (read != size)
                throw new RuntimeException(String.format(Locale.US, "Not correct size, expected %d, but was %d", size, read));

            return floats;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private float nextFloat(LittleEndianDataInputStream dis) {
        try {
            float f = dis.readFloat();
            return f;
        } catch (IOException e) {
            return Float.MIN_VALUE;
        }
    }
}

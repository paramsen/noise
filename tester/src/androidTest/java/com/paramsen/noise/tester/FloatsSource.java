package com.paramsen.noise.tester;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
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
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(input))) {
            int size = dis.readInt();
            float[] floats = new float[size];

            float next = 0f;
            int read = 0;

            while ((next = nextFloat(dis)) > Float.MIN_VALUE) {
                floats[read++] = next;
            }

            if(read != size - 1)
                throw new RuntimeException(String.format(Locale.US, "Not correct size, expected %d, but was %d", size, read));

            return floats;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private float nextFloat(DataInputStream dis) {
        try {
            return dis.readFloat();
        } catch (IOException e) {
            return Float.MIN_VALUE;
        }
    }
}

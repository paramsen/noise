# Noise
_A FFT computation library for Android_

Noise is an Android wrapper for kissfft, a FFT implementation written in C.
Noise features an api that is designed to be easy to use, and familiar for Android devs.
However, a low level JNI api is available as well.

## Sample app
..

## Instructions

This lib is a Java api for kissfft, consult the kissfft readme as well or if you want
more information about the internal FFT implementation.

#### Instantiate Noise

Noise supports computing DFT from real and imaginary input data, through either a threadsafe or 
optimized implementation. An optimized instance computes DFT:s at half the time and should be fit
for most use cases. Threadsafe instances can compute DFT:s concurrently for variable input sizes, 
but has an overhead of allocating memory for each invocation.

#### Real input

Instantiate an optimized instance, that is configured to compute DFT:s on input arrays of size 4096
and that internally manages an output array.
```
Noise noise = Noise.real()
    .optimized()
    .init(4096, true); //input size == 4096, internal output array
```

Compute a DFT.

```
float[] realInput = new float[4096];
    
// .. fill realInput with data
    
// Compute DFT from realInput:
    
float[] computed = noise.fft(realInput);
    
// The output array has the pairs of real+imaginary floats in a one dimensional array; even indeces
// are real, odd indeces are imaginary. DC bin is located at index 0, 1, nyquist at index n-2, n-1
    
for(int i = 0; i < computed.length / 2; i++) {
    float real = computed[i * 2];
    float imaginary = computed[i * 2 + 1];
    
    System.out.printf("index: %d, real: %.5f, imaginary: %.5f\n", i, real, imaginary);
}

```

#### Imaginary input

Instantiate an optimized instance, that is configured to compute DFT:s on input arrays of size
8192 (4096 [real, imaginary] pairs) and that internally manages an output array.
```
Noise noise = Noise.real()
    .optimized()
    .init(8192, true); //input size == 8192, internal output array
```

In order to compute a DFT from imaginary input, we need to structure our real+imaginary pairs in a 
flat, one dimensional array. Thus the input array has pairs of real+imaginary like; 
float[0] = firstReal, float[1] = firstImaginary, float[2] = secondReal, float[3] = secondImaginary..
```
float[] imaginaryInput = new float[8192];
    
// fill imaginaryInput with data (pairs is an array of pairs with [real, imaginary] objects):
    
for(int i = 0; i < pairs.length; i++) {
    imaginaryInput[i * 2] = pairs[i].real;
    imaginaryInput[i * 2 + 1] = pairs[i].imaginary;
}
    
// Compute DFT from imaginaryInput:
    
float[] computed = noise.fft(realInput);
    
// The output array has the pairs of real+imaginary floats in a one dimensional array; even indeces
// are real, odd indeces are imaginary. DC bin is located at index 0, 1, nyquist at index n/2-2, n/2-1
    
for(int i = 0; i < computed.length / 2; i++) {
    float real = computed[i * 2];
    float imaginary = computed[i * 2 + 1];
    
    System.out.printf("index: %d, real: %.5f, imaginary: %.5f\n", i, real, imaginary);
}

```

## Get started

Include in Android Studio < 3.0 projects

    compile 'com.paramsen.noise:<version>'

Or for Android Studio >= 3.0 with Gradle 4  projects

    implementation 'com.paramsen.noise:<version>'


## Advanced
..


## License
..


## Notes
..
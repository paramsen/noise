#include <jni.h>
#include <iostream>
#include <android/log.h>
#include <kiss_fft.h>
#include "tools/kiss_fftr.h"

extern "C" {

/**
 * Pre alloc this struct and pass a pointer in order to reuse
 */
struct optimized {
    kiss_fft_cpx *result;
    kiss_fftr_cfg config;
    jfloat *flat;
};

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_nReal(JNIEnv *env, jobject jThis, jfloatArray jInput,
                                                jfloatArray jOutput) {
    jsize inSize = env->GetArrayLength(jInput);
    jsize outSize = env->GetArrayLength(jOutput);

    if (outSize != inSize + 2)
        __android_log_print(ANDROID_LOG_ERROR, "NATIVE_NOISE",
                            "output len must be (inSize + 2). Read javadoc.");
    if (inSize & 1)
        __android_log_print(ANDROID_LOG_ERROR, "NATIVE_NOISE",
                            "kissfft require input length to be even");

    float *input = env->GetFloatArrayElements(jInput, 0);

    kiss_fftr_cfg config = kiss_fftr_alloc(inSize, 0, 0, 0);
    kiss_fft_cpx *result = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * outSize);
    kiss_fftr(config, input, result);
    jfloat *flat = (jfloat *) malloc(sizeof(jfloat) * outSize);

    for (int i = 0; i < outSize; ++i)
        if (i % 2 == 0)
            flat[i] = result[i].r;
        else
            flat[i] = result[i].i;

    env->SetFloatArrayRegion(jOutput, 0, outSize, flat);
    env->ReleaseFloatArrayElements(jInput, input, 0);
    free(config);
    free(result);
    free(flat);
}

kiss_fft_cpx *result = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * 4098);
kiss_fftr_cfg config = kiss_fftr_alloc(4096, 0, 0, 0);
jfloat *flat = (jfloat *) malloc(sizeof(jfloat) * 4098);

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_nRealOpt(JNIEnv *env, jobject jThis, jfloatArray jInput,
                                                jfloatArray jOutput) {
    jsize inSize = env->GetArrayLength(jInput);
    jsize outSize = env->GetArrayLength(jOutput);

    if (outSize != inSize + 2)
        __android_log_print(ANDROID_LOG_ERROR, "NATIVE_NOISE",
                            "output len must be (inSize + 2). Read javadoc.");
    if (inSize & 1)
        __android_log_print(ANDROID_LOG_ERROR, "NATIVE_NOISE",
                            "kissfft require input length to be even");

    float *input = env->GetFloatArrayElements(jInput, 0);

    kiss_fftr(config, input, result);

    for (int i = 0; i < outSize; ++i)
        if (i % 2 == 0)
            flat[i] = result[i].r;
        else
            flat[i] = result[i].i;

    env->SetFloatArrayRegion(jOutput, 0, outSize, flat);
    env->ReleaseFloatArrayElements(jInput, input, 0);
}

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_nImaginary(JNIEnv *env, jobject jThis, jfloatArray jInput,
                                                     jfloatArray jOutput) {
    jsize inSize = env->GetArrayLength(jInput);
    jsize outSize = env->GetArrayLength(jOutput);

    if (inSize != outSize)
        __android_log_print(ANDROID_LOG_ERROR, "NATIVE_NOISE",
                            "output len must equal input len. Read javadoc.");
    if (inSize & 1)
        __android_log_print(ANDROID_LOG_ERROR, "NATIVE_NOISE",
                            "kissfft require input length to be even");

    float *input = env->GetFloatArrayElements(jInput, 0);
    kiss_fft_cpx *fftInput = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * inSize);

    for (int i = 0; i < inSize / 2; ++i)
        if (i % 2 == 0)
            fftInput[i].r = input[i];
        else
            fftInput[i].i = input[i];

    kiss_fft_cfg config = kiss_fft_alloc(inSize, 0, 0, 0);
    kiss_fft_cpx *result = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * outSize);
    kiss_fft(config, fftInput, result);
    jfloat *flat = (jfloat *) malloc(sizeof(jfloat) * outSize);

    for (int i = 0; i < outSize; ++i)
        if (i % 2 == 0)
            flat[i] = result[i].r;
        else
            flat[i] = result[i].i;

    env->SetFloatArrayRegion(jOutput, 0, outSize, flat);
    env->ReleaseFloatArrayElements(jInput, input, 0);
    free(config);
    free(fftInput);
    free(result);
    free(flat);
}
}
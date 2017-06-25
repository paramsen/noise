#include <jni.h>
#include <iostream>
#include <android/log.h>
#include <kiss_fft.h>
#include "tools/kiss_fftr.h"

extern "C" {

/**
 * Pre alloc this struct and pass a pointer in order to reuse
 */
typedef struct {
    kiss_fft_cpx *result;
    kiss_fftr_cfg config;
} NoiseOptimizedRealCfg;

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_nRealThreadSafe(JNIEnv *env, jobject jThis,
                                                          jfloatArray jInput,
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
    jfloat *output = env->GetFloatArrayElements(jOutput, 0);

    kiss_fftr_cfg config = kiss_fftr_alloc(inSize, 0, 0, 0);
    kiss_fft_cpx *result = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * outSize);
    kiss_fftr(config, input, result);

    for (int i = 0; i < outSize; ++i)
        if (i % 2 == 0)
            output[i] = result[i].r;
        else
            output[i] = result[i].i;

    env->SetFloatArrayRegion(jOutput, 0, outSize, output);
    env->ReleaseFloatArrayElements(jInput, input, 0);
    env->ReleaseFloatArrayElements(jOutput, output, 0);
    free(config);
    free(result);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_nRealOptimizedCfg(JNIEnv *env, jobject jThis, jint inSize) {
    NoiseOptimizedRealCfg *cfg = (NoiseOptimizedRealCfg *) malloc(sizeof(NoiseOptimizedRealCfg));
    cfg->config = kiss_fftr_alloc(inSize, 0, 0, 0);
    cfg->result = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * inSize + 2);

    return (jlong) cfg;
}

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_nRealOptimizedCfgDispose(JNIEnv *env, jobject jThis, jlong cfgPointer) {
    NoiseOptimizedRealCfg *cfg = (NoiseOptimizedRealCfg *) cfgPointer;

    free(cfg->config);
    free(cfg->result);
    free(cfg);
}

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_nRealOptimized(JNIEnv *env, jobject jThis,
                                                         jfloatArray jInput,
                                                         jfloatArray jOutput, jlong cfgPointer) {
    jsize inSize = env->GetArrayLength(jInput);
    jsize outSize = env->GetArrayLength(jOutput);

    if (outSize != inSize + 2)
        __android_log_print(ANDROID_LOG_ERROR, "NATIVE_NOISE",
                            "output len must be (inSize + 2). Read javadoc.");
    if (inSize & 1)
        __android_log_print(ANDROID_LOG_ERROR, "NATIVE_NOISE",
                            "kissfft require input length to be even");

    float *input = env->GetFloatArrayElements(jInput, 0);
    jfloat *output = env->GetFloatArrayElements(jOutput, 0);
    NoiseOptimizedRealCfg *cfg = (NoiseOptimizedRealCfg *) cfgPointer;

    kiss_fftr(cfg->config, input, cfg->result);

    for (int i = 0; i < outSize; ++i)
        if (i % 2 == 0)
            output[i] = cfg->result[i].r;
        else
            output[i] = cfg->result[i].i;

    env->ReleaseFloatArrayElements(jInput, input, 0);
    env->ReleaseFloatArrayElements(jOutput, output, 0);
}

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_nImaginaryThreadSafe(JNIEnv *env, jobject jThis,
                                                               jfloatArray jInput,
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
    float *output = env->GetFloatArrayElements(jOutput, 0);
    kiss_fft_cpx *fftInput = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * inSize);

    for (int i = 0; i < inSize / 2; ++i)
        if (i % 2 == 0)
            fftInput[i].r = input[i];
        else
            fftInput[i].i = input[i];

    kiss_fft_cfg config = kiss_fft_alloc(inSize, 0, 0, 0);
    kiss_fft_cpx *result = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * outSize);
    kiss_fft(config, fftInput, result);

    for (int i = 0; i < outSize; ++i)
        if (i % 2 == 0)
            output[i] = result[i].r;
        else
            output[i] = result[i].i;

    env->SetFloatArrayRegion(jOutput, 0, outSize, output);
    env->ReleaseFloatArrayElements(jInput, input, 0);
    env->ReleaseFloatArrayElements(jOutput, output, 0);
    free(config);
    free(fftInput);
    free(result);
}
}
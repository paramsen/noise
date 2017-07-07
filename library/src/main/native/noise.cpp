#include <jni.h>
#include <iostream>
#include <android/log.h>
#include <kiss_fft.h>
#include "tools/kiss_fftr.h"

extern "C" {

typedef struct {
    kiss_fft_cpx *result;
    kiss_fftr_cfg config;
} NoiseOptimizedRealCfg;

typedef struct {
    kiss_fft_cpx *result;
    kiss_fft_cpx *fftInput;
    kiss_fft_cfg config;
} NoiseOptimizedImaginaryCfg;

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_realThreadSafe(JNIEnv *env, jobject jThis,
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

    for (int i = 0; i < outSize / 2; ++i) {
        output[i * 2] = result[i].r;
        output[i * 2 + 1] = result[i].i;
    }

    env->SetFloatArrayRegion(jOutput, 0, outSize, output);
    env->ReleaseFloatArrayElements(jInput, input, 0);
    env->ReleaseFloatArrayElements(jOutput, output, 0);
    free(config);
    free(result);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_realOptimizedCfg(JNIEnv *env, jobject jThis,
                                                           jint inSize) {
    NoiseOptimizedRealCfg *cfg = (NoiseOptimizedRealCfg *) malloc(sizeof(NoiseOptimizedRealCfg));
    cfg->config = kiss_fftr_alloc(inSize, 0, 0, 0);
    cfg->result = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * inSize + 2);

    return (jlong) cfg;
}

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_realOptimizedCfgDispose(JNIEnv *env, jobject jThis,
                                                                  jlong cfgPointer) {
    NoiseOptimizedRealCfg *cfg = (NoiseOptimizedRealCfg *) cfgPointer;

    free(cfg->config);
    free(cfg->result);
    free(cfg);
}

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_realOptimized(JNIEnv *env, jobject jThis,
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

    for (int i = 0; i < outSize / 2; ++i) {
        output[i * 2] = cfg->result[i].r;
        output[i * 2 + 1] = cfg->result[i].i;
    }

    env->ReleaseFloatArrayElements(jInput, input, 0);
    env->ReleaseFloatArrayElements(jOutput, output, 0);
}

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_imaginaryThreadSafe(JNIEnv *env, jobject jThis,
                                                              jfloatArray jInput,
                                                              jfloatArray jOutput) {
    jsize inSize = env->GetArrayLength(jInput);
    jsize outSize = env->GetArrayLength(jOutput);

    if (outSize != inSize)
        __android_log_print(ANDROID_LOG_ERROR, "NATIVE_NOISE",
                            "output len must equal input len. Read javadoc.");

    float *input = env->GetFloatArrayElements(jInput, 0);
    float *output = env->GetFloatArrayElements(jOutput, 0);
    kiss_fft_cpx *fftInput = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * inSize / 2);

    for (int i = 0; i < inSize / 2; i++) {
        fftInput[i].r = input[i * 2];
        fftInput[i].i = input[i * 2 + 1];
    }

    kiss_fft_cfg config = kiss_fft_alloc(inSize / 2, 0, 0, 0);
    kiss_fft_cpx *result = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * outSize / 2);
    kiss_fft(config, fftInput, result);

    for (int i = 0; i < outSize / 2; i++) {
        output[i * 2] = result[i].r;
        output[i * 2 + 1] = result[i].i;
    }

    env->SetFloatArrayRegion(jOutput, 0, outSize, output);
    env->ReleaseFloatArrayElements(jInput, input, 0);
    env->ReleaseFloatArrayElements(jOutput, output, 0);
    free(config);
    free(fftInput);
    free(result);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_imaginaryOptimizedCfg(JNIEnv *env, jobject jThis,
                                                                jint inSize) {
    NoiseOptimizedImaginaryCfg *cfg = (NoiseOptimizedImaginaryCfg *) malloc(
            sizeof(NoiseOptimizedImaginaryCfg));
    cfg->config = kiss_fft_alloc(inSize / 2, 0, 0, 0);
    cfg->fftInput = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * inSize / 2);
    cfg->result = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * inSize / 2);

    return (jlong) cfg;
}

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_imaginaryOptimizedCfgDispose(JNIEnv *env, jobject jThis,
                                                                       jlong cfgPointer) {
    NoiseOptimizedImaginaryCfg *cfg = (NoiseOptimizedImaginaryCfg *) cfgPointer;

    free(cfg->config);
    free(cfg->fftInput);
    free(cfg->result);
    free(cfg);
}

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_imaginaryOptimized(JNIEnv *env, jobject jThis,
                                                             jfloatArray jInput,
                                                             jfloatArray jOutput,
                                                             jlong cfgPointer) {
    jsize inSize = env->GetArrayLength(jInput);
    jsize outSize = env->GetArrayLength(jOutput);

    if (outSize != inSize)
        __android_log_print(ANDROID_LOG_ERROR, "NATIVE_NOISE",
                            "output len (%d) must equal input len (%d). Read javadoc.", outSize, inSize);

    float *input = env->GetFloatArrayElements(jInput, 0);
    float *output = env->GetFloatArrayElements(jOutput, 0);
    NoiseOptimizedImaginaryCfg *cfg = (NoiseOptimizedImaginaryCfg *) cfgPointer;

    for (int i = 0; i < inSize / 2; i++) {
        cfg->fftInput[i].r = input[i * 2];
        cfg->fftInput[i].i = input[i * 2 + 1];
    }

    kiss_fft(cfg->config, cfg->fftInput, cfg->result);

    for (int i = 0; i < outSize / 2; i++) {
        output[i * 2] = cfg->result[i].r;
        output[i * 2 + 1] = cfg->result[i].i;
    }

    env->SetFloatArrayRegion(jOutput, 0, outSize, output);
    env->ReleaseFloatArrayElements(jInput, input, 0);
    env->ReleaseFloatArrayElements(jOutput, output, 0);
}
}
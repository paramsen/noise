#include <jni.h>
#include <android/log.h>
#include <kiss_fft.h>
#include "tools/kiss_fftr.h"

typedef struct {
    kiss_fft_cpx *result;
    kiss_fftr_cfg config;
} NoiseRealConfig;

typedef struct {
    kiss_fft_cpx *result;
    kiss_fft_cpx *fftInput;
    kiss_fft_cfg config;
} NoiseImaginaryConfig;

JNIEXPORT jlong JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_realConfig(JNIEnv *env, jclass jThis,
                                                           jint inSize) {
    NoiseRealConfig *cfg = (NoiseRealConfig *) malloc(sizeof(NoiseRealConfig));
    cfg->config = kiss_fftr_alloc(inSize, 0, 0, 0);
    cfg->result = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * inSize + 2);

    return (jlong) cfg;
}

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_realConfigDispose(JNIEnv *env, jclass jThis,
                                                                  jlong cfgPointer) {
    NoiseRealConfig *cfg = (NoiseRealConfig *) cfgPointer;

    free(cfg->config);
    free(cfg->result);
    free(cfg);
}

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_real(JNIEnv *env, jclass jThis,
                                                        jfloatArray jInput,
                                                        jfloatArray jOutput, jlong cfgPointer) {
    jsize inSize = (*env)->GetArrayLength(env, jInput);
    jsize outSize = (*env)->GetArrayLength(env, jOutput);

    if (outSize != inSize + 2)
        __android_log_print(ANDROID_LOG_ERROR, "NATIVE_NOISE",
                            "output len must be (inSize + 2). Read javadoc.");
    if (inSize & 1)
        __android_log_print(ANDROID_LOG_ERROR, "NATIVE_NOISE",
                            "kissfft require input length to be even");

    float *input = (*env)->GetFloatArrayElements(env, jInput, 0);
    jfloat *output = (*env)->GetFloatArrayElements(env, jOutput, 0);
    NoiseRealConfig *cfg = (NoiseRealConfig *) cfgPointer;

    kiss_fftr(cfg->config, input, cfg->result);

    for (int i = 0; i < outSize / 2; ++i) {
        output[i * 2] = cfg->result[i].r;
        output[i * 2 + 1] = cfg->result[i].i;
    }

    (*env)->ReleaseFloatArrayElements(env, jInput, input, 0);
    (*env)->ReleaseFloatArrayElements(env, jOutput, output, 0);
}

JNIEXPORT jlong JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_imaginaryConfig(JNIEnv *env, jclass jThis,
                                                                jint inSize) {
    NoiseImaginaryConfig *cfg = (NoiseImaginaryConfig *) malloc(
            sizeof(NoiseImaginaryConfig));
    cfg->config = kiss_fft_alloc(inSize / 2, 0, 0, 0);
    cfg->fftInput = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * inSize / 2);
    cfg->result = (kiss_fft_cpx *) malloc(sizeof(kiss_fft_cpx) * inSize / 2);

    return (jlong) cfg;
}

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_imaginaryConfigDispose(JNIEnv *env, jclass jThis,
                                                                       jlong cfgPointer) {
    NoiseImaginaryConfig *cfg = (NoiseImaginaryConfig *) cfgPointer;

    free(cfg->config);
    free(cfg->fftInput);
    free(cfg->result);
    free(cfg);
}

JNIEXPORT void JNICALL
Java_com_paramsen_noise_NoiseNativeBridge_imaginary(JNIEnv *env, jclass jThis,
                                                             jfloatArray jInput,
                                                             jfloatArray jOutput,
                                                             jlong cfgPointer) {
    jsize inSize = (*env)->GetArrayLength(env, jInput);
    jsize outSize = (*env)->GetArrayLength(env, jOutput);

    if (outSize != inSize)
        __android_log_print(ANDROID_LOG_ERROR, "NATIVE_NOISE",
                            "output len (%d) must equal input len (%d). Read javadoc.", outSize,
                            inSize);

    float *input = (*env)->GetFloatArrayElements(env, jInput, 0);
    float *output = (*env)->GetFloatArrayElements(env, jOutput, 0);
    NoiseImaginaryConfig *cfg = (NoiseImaginaryConfig *) cfgPointer;

    for (int i = 0; i < inSize / 2; i++) {
        cfg->fftInput[i].r = input[i * 2];
        cfg->fftInput[i].i = input[i * 2 + 1];
    }

    kiss_fft(cfg->config, cfg->fftInput, cfg->result);

    for (int i = 0; i < outSize / 2; i++) {
        output[i * 2] = cfg->result[i].r;
        output[i * 2 + 1] = cfg->result[i].i;
    }

    (*env)->SetFloatArrayRegion(env, jOutput, 0, outSize, output);
    (*env)->ReleaseFloatArrayElements(env, jInput, input, 0);
    (*env)->ReleaseFloatArrayElements(env, jOutput, output, 0);
}
package com.paramsen.noise.sample.source

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

/**
 * Rx Flowable factory that expose a Flowable through stream() that while subscribed to emits
 * AudioWindows of size 4096 at approx 44.1khz. Uses Disposable to handle deallocation
 *
 * @author Pär Amsen 06/2017
 */
class AudioSource {
    val RATE_HZ = 44100
    val FPS = 60
    val SAMPLE_SIZE = RATE_HZ / FPS

    private val flowable: Flowable<FloatArray>

    init {
        flowable = Flowable.create<FloatArray>({ sub ->
            val src = MediaRecorder.AudioSource.MIC
            val cfg = AudioFormat.CHANNEL_IN_MONO
            val format = AudioFormat.ENCODING_PCM_16BIT
            val size = AudioRecord.getMinBufferSize(RATE_HZ, cfg, format)

            if (size <= 0) {
                sub.onError(RuntimeException("AudioSource / Could not allocate audio buffer on this device (emulator? no mic?)"))
                return@create
            }

            val recorder = AudioRecord(src, RATE_HZ, cfg, format, size)

            recorder.startRecording()
            sub.setCancellable({
                recorder.stop()
                recorder.release()
            })

            val buf = ShortArray(SAMPLE_SIZE)
            val out = FloatArray(SAMPLE_SIZE)
            var read = 0

            while (!sub.isCancelled) {
                read += recorder.read(buf, read, SAMPLE_SIZE - read)

                if (read == SAMPLE_SIZE) {
                    for (i in 0..SAMPLE_SIZE - 1) {
                        out[i] = buf[i].toFloat()
                    }

                    sub.onNext(out)
                    read = 0
                }
            }

            sub.onComplete()
        }, BackpressureStrategy.DROP)
                .subscribeOn(Schedulers.io())
                .share()
    }

    /**
     * All subscribers must unsubscribe in order for Flowable to cancel the microphone stream. The
     * stream is started automatically when subscribed to, the same mic stream is used for all subs.
     */
    fun stream(): Flowable<FloatArray> {
        return flowable
    }
}
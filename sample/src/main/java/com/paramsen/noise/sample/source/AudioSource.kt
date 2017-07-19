package com.paramsen.noise.sample.source

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.nio.FloatBuffer

const val RATE_HZ = 44100
const val SAMPLE_SIZE = 4096

/**
 * Rx Flowable factory that expose a Flowable through stream() that while subscribed to emits
 * audio frames of size 4096 and 768 [~10fps, ~60fps]. Uses Disposable to handle deallocation.
 *
 * @author Pär Amsen 06/2017
 */
class AudioSource() {
    private val flowable: Flowable<FloatArray>

    /**
     * The returned Flowable publish frames of two sizes; 4096 and 768. Roughly 10fps / 60fps.
     * Filter is used to distinguish the two types. Ideally this should be handled in two separate
     * Flowables, but AudioRecord makes that utterly complex.
     */
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

            val buf = ShortArray(512)
            val out = FloatBuffer.allocate(SAMPLE_SIZE)
            var read = 0

            while (!sub.isCancelled) {
                read += recorder.read(buf, read, buf.size - read)

                if (read == buf.size) {
                    for (i in 0..buf.size - 1) {
                        out.put(buf[i].toFloat())
                    }

                    if (!out.hasRemaining()) {
                        val cpy = FloatArray(out.array().size)
                        System.arraycopy(out.array(), 0, cpy, 0, out.array().size)
                        sub.onNext(cpy)
                        out.clear()
                    }

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
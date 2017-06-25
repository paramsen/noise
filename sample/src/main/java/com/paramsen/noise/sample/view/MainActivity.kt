package com.paramsen.noise.sample.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.paramsen.noise.Noise
import com.paramsen.noise.sample.R
import com.paramsen.noise.sample.source.AudioSource
import com.paramsen.noise.sample.source.SAMPLE_SIZE
import io.reactivex.Flowable
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (requestAudio())
            start()
    }

    private fun start() {
        val src = AudioSource().stream()
        val noise = Noise.real().optimized().init(4096, true)

        //AudioView
        src.subscribe(audioView::onWindow, { e -> Log.e(TAG, e.message) })
        //Log throughput
        src.window(10, TimeUnit.SECONDS)
                .flatMapSingle { o -> o.count().map { c -> c * SAMPLE_SIZE / 10 } }
                .onErrorReturn { -1 }
                .subscribe { count -> Log.d(TAG, "AudioSource throughput: ~$count/s") }
        //FFTView
        src.compose(this::accumulate)
                .filter { fft -> fft.size == 4096 }
                .map(noise::fft)
                .doOnNext{e -> println(e[4090])}
                .subscribe(fftView::onFFT, { e -> Log.e(TAG, e.message) })
    }

    /**
     * Output windows of 4096 len, ~10/sec for 44.1khz
     */
    private fun accumulate(o: Flowable<FloatArray>): Flowable<FloatArray> {
        return o.map(object : Function<FloatArray, FloatArray> {
            val buf = ArrayDeque<Float>()
            val out = FloatArray(4096)

            override fun apply(window: FloatArray): FloatArray {
                window.forEach(buf::addLast)

                if (buf.size >= out.size) {
                    for (i in 0..out.size - 1)
                        out[i] = buf.pop()

                    return out
                }

                return FloatArray(0)
            }
        })
    }

    private fun requestAudio(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1337)
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            start()
    }
}

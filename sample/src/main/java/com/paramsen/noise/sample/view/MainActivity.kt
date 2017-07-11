package com.paramsen.noise.sample.view

import android.Manifest.permission.RECORD_AUDIO
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.paramsen.noise.Noise
import com.paramsen.noise.sample.R
import com.paramsen.noise.sample.source.AudioSource
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName!!

    val disposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        if (requestAudio() && disposable.size() == 0)
            start()
    }

    override fun onStop() {
        stop()
        super.onStop()
    }

    /**
     * Subscribe to microphone
     */
    private fun start() {
        val src = AudioSource().stream()
        val noise = Noise.real().optimized().init(4096, true)

        //AudioView
        disposable.add(src.subscribe(audioView::onWindow, { e -> Log.e(TAG, e.message) }))
        //FFTView
        disposable.add(src.compose(this::accumulate)
                .map(noise::fft)
                .observeOn(Schedulers.computation())
                .subscribe({ fft ->
                    fftHeatMapView.onFFT(fft)
                    fftBandView.onFFT(fft)
                }, { e -> Log.e(TAG, e.message) }))
    }

    /**
     * Dispose microphone subscriptions
     */
    private fun stop() {
        disposable.clear()
    }

    /**
     * Output windows of 4096 len, ~10/sec for 44.1khz
     */
    private fun accumulate(o: Flowable<FloatArray>): Flowable<FloatArray> {
        return o.map(object : Function<FloatArray, FloatArray> {
            val buf = ArrayDeque<Float>()
            val out = FloatArray(4096)

            override fun apply(window: FloatArray): FloatArray {
                window.map { it * 10 }.forEach(buf::addLast)

                if (buf.size >= out.size) {
                    for (i in 0..out.size - 1)
                        out[i] = buf.pop()

                    return out
                }

                return FloatArray(0)
            }
        }).filter { fft -> fft.size == 4096 } //filter only the emissions of complete 4096 windows
    }

    private fun requestAudio(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(this, RECORD_AUDIO) != PERMISSION_GRANTED) {
            requestPermissions(arrayOf(RECORD_AUDIO), 1337)
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults[0] == PERMISSION_GRANTED)
            start()
    }
}

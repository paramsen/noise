package com.paramsen.noise.sample.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.paramsen.noise.sample.R
import com.paramsen.noise.sample.source.AudioSource
import com.paramsen.noise.sample.source.SAMPLE_SIZE
import kotlinx.android.synthetic.main.activity_main.*
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

        //AudioView
        src.subscribe(audioView::onWindow, { e -> Log.e(TAG, e.message) })
        //Log throughput
        src.window(10, TimeUnit.SECONDS)
                .flatMapSingle { o -> o.count().map { c -> c * SAMPLE_SIZE / 10 } }
                .onErrorReturn { -1 }
                .subscribe { count -> Log.d(TAG, "AudioSource throughput: ~$count/s") }
        //FFTView
        src.subscribe(fftView::onWindow, { e -> Log.e(TAG, e.message) })
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

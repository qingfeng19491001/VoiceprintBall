package com.voiceprintball.activity

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.voiceprintball.R
import com.voiceprintball.view.VoiceWaveView
import kotlin.random.Random

/**
 * 声纹小球动画示例页面
 * 展示 VoiceWaveView 的使用方法：
 * 1. 待机状态：小球轻微起伏
 * 2. 手动控制：通过 SeekBar 控制振幅
 * 3. 自动模拟：模拟语音输入时的振幅变化
 */
class WaveformActivity : AppCompatActivity() {

    private lateinit var waveformView: VoiceWaveView
    private lateinit var seekBar: SeekBar
    private lateinit var btnAuto: Button
    private lateinit var btnStop: Button

    private var autoAnimator: ValueAnimator? = null
    private var autoHandler: Handler? = null
    private var autoRunnable: Runnable? = null
    private var isAutoRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waveform)

        waveformView = findViewById(R.id.waveform_view)
        seekBar = findViewById(R.id.seek_bar)
        btnAuto = findViewById(R.id.btn_auto)
        btnStop = findViewById(R.id.btn_stop)

        // 设置小球颜色
        waveformView.setBallColor(0xFFFFFFFF.toInt())

        // SeekBar 控制振幅
        seekBar.max = 100
        seekBar.progress = 0
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && !isAutoRunning) {
                    waveformView.pushAmplitude01(progress / 100f)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                stopAutoMode()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 自动模拟按钮
        btnAuto.setOnClickListener {
            startAutoMode()
        }

        // 停止按钮
        btnStop.setOnClickListener {
            stopAutoMode()
            seekBar.progress = 0
            waveformView.pushAmplitude01(0f)
        }
    }

    private fun startAutoMode() {
        if (isAutoRunning) return
        isAutoRunning = true
        btnAuto.isEnabled = false
        btnStop.isEnabled = true

        autoHandler = Handler(Looper.getMainLooper())
        autoRunnable = object : Runnable {
            override fun run() {
                if (isAutoRunning) {
                    // 随机生成 0-1 的振幅值，模拟语音输入
                    val amplitude = Random.nextFloat()
                    waveformView.pushAmplitude01(amplitude)
                    seekBar.progress = (amplitude * 100).toInt()
                    autoHandler?.postDelayed(this, 100) // 每 100ms 更新一次
                }
            }
        }
        autoHandler?.post(autoRunnable!!)
    }

    private fun stopAutoMode() {
        isAutoRunning = false
        autoHandler?.removeCallbacks(autoRunnable!!)
        btnAuto.isEnabled = true
        btnStop.isEnabled = false
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAutoMode()
        autoHandler = null
        autoRunnable = null
    }
}



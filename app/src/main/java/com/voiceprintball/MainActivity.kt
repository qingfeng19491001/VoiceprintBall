package com.voiceprintball

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.voiceprintball.activity.WaveformActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 直接跳转到 WaveformActivity
        startActivity(Intent(this, WaveformActivity::class.java))
        finish()
    }
}
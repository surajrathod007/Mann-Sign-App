package com.surajmanshal.mannsign.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.surajmanshal.mannsign.MainActivity
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.SecuredScreenActivity
import com.surajmanshal.mannsign.databinding.ActivitySplashBinding

class IntroActivity : SecuredScreenActivity() {

    lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val videoView = binding.videoView
        val videoPath = "android.resource://" + packageName + "/" + R.raw.splash_video
        val videoUri = Uri.parse(videoPath)
        videoView.setVideoURI(videoUri)

        // Start video playback
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = false
            mediaPlayer.start()
        }

        // Handle video playback completion
        videoView.setOnCompletionListener { mediaPlayer ->
            mediaPlayer.stop()
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
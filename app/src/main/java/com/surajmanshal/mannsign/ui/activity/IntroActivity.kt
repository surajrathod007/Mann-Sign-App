package com.surajmanshal.mannsign.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.datastore.preferences.core.stringPreferencesKey
import com.surajmanshal.mannsign.AuthenticationActivity
import com.surajmanshal.mannsign.MainActivity
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.SecuredScreenActivity
import com.surajmanshal.mannsign.databinding.ActivitySplashBinding
import com.surajmanshal.mannsign.utils.auth.DataStore
import com.surajmanshal.mannsign.utils.auth.DataStore.preferenceDataStoreAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class IntroActivity : SecuredScreenActivity() {

    lateinit var binding: ActivitySplashBinding

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
//            navigateToMainActivity()
            CoroutineScope(Dispatchers.IO).launch {
                val token = isLoggedIn(DataStore.JWT_TOKEN)
                if (!token.isNullOrEmpty()) {
                    navigateToMainActivity(token)
                } else {
                    navigateToAuthActivity()
                }
            }
        }
    }

    suspend fun isLoggedIn(key: String): String? {
        val data = preferenceDataStoreAuth.data.first()
        return data[stringPreferencesKey(key)]
    }

    private fun navigateToMainActivity(token: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(DataStore.JWT_TOKEN, token)
        startActivity(intent)
        finish()
    }

    private fun navigateToAuthActivity() {
        val intent = Intent(this, AuthenticationActivity::class.java)
        startActivity(intent)
        finish()
    }
}
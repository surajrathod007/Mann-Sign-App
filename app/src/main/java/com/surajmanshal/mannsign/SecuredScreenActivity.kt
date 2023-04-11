package com.surajmanshal.mannsign

import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

open class SecuredScreenActivity : AppCompatActivity() {

    private val preventScreenShot = WindowManager.LayoutParams.FLAG_SECURE

    override fun onResume() {
        super.onResume()
        window.setFlags(preventScreenShot,preventScreenShot)
    }
}
package com.surajmanshal.mannsign

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.onesignal.OSPermissionObserver
import com.onesignal.OSPermissionStateChanges
import com.onesignal.OneSignal
import com.surajmanshal.mannsign.utils.Functions

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_USER_APP_ID)

        //Functions.makeToast(this,OneSignal.getDeviceState()?.userId.toString())
    }

    companion object{
        const val ONESIGNAL_USER_APP_ID = "87353ca3-0d82-4a23-9ef6-76e162617243"
    }
}
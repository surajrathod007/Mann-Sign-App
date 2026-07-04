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
        OneSignal.setAppId(BuildConfig.ONESIGNAL_APP_ID)

        //Functions.makeToast(this,OneSignal.getDeviceState()?.userId.toString())
    }
}
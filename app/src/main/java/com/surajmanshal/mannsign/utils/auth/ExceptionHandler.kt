package com.surajmanshal.mannsign.utils.auth

import android.content.Context
import android.util.Log
import android.widget.Toast


object ExceptionHandler {
    fun catchOnLog(e:String){
        Log.d("catchOnLog", e)
    }
    fun catchOnContext(context : Context, e: String){
        val toast = Toast.makeText(context, e, Toast.LENGTH_LONG)
            toast.show()
    }
}
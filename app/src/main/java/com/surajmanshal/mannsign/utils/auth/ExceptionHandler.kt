package com.surajmanshal.mannsign.utils.auth

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.surajmanshal.mannsign.R


object ExceptionHandler {
    fun catchOnLog(e:String){
        Log.d("catchOnLog", e)
    }
    fun catchOnContext(context : Context, e: String){
        val toast = Toast.makeText(context, e, Toast.LENGTH_LONG)
        val view: View? = toast.view
        if (view != null) {
            view.background = context.resources.getDrawable(R.drawable.error_toast_bg)
            toast.show()
        }
    }
}
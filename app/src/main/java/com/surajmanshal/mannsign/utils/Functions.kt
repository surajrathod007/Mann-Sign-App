package com.surajmanshal.mannsign.utils

import android.content.Context
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.surajmanshal.mannsign.URL

object Functions {
    fun urlMaker(imageurl :String): String {
        val fileName = imageurl.substringAfter("http://localhost:8700/images/")
        return URL.IMAGE_PATH+ fileName
    }
    fun setTypeNumber(editText: EditText){
        editText.inputType = InputType.TYPE_CLASS_NUMBER
    }

    fun makeViewVisible(view : View){
        view.visibility = View.VISIBLE
    }
    fun makeViewGone(view : View){
        view.visibility = View.GONE
    }
    fun makeToast(context : Context, msg : String,long : Boolean=false){
        if(long){
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
        }
    }
}
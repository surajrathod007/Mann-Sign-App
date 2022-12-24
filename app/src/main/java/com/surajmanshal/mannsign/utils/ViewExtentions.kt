package com.surajmanshal.mannsign.utils

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide

fun View.show(){
    this.isVisible = true
}

fun View.hide(){
    this.isVisible = false
}

fun EditText.isFilled() = this.text.isNotEmpty()

fun EditText.clear() = this.text.clear()

fun ImageView.loadImageWithUrl(imageUrl :String){
    Glide.with(this.context).load(Functions.urlMaker(imageUrl)).into(this)
}

fun ImageView.loadRoundedImageWithUrl(imageUrl :String){
    Glide.with(this.context).load(Functions.urlMaker(imageUrl)).circleCrop().into(this)
}
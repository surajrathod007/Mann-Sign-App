package com.surajmanshal.mannsign.utils

import android.view.View
import android.widget.EditText
import androidx.core.view.isVisible

fun View.show(){
    this.isVisible = true
}

fun View.hide(){
    this.isVisible = false
}

fun EditText.isFilled() = this.text.isNotEmpty()

fun EditText.clear() = this.text.clear()

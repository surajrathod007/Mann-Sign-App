package com.surajmanshal.mannsign.utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.util.Pair
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.ui.activity.ImageViewingActivity

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

fun ImageView.viewFullScreen(activity: Activity,imgUrl : String){

        val optionsBundle = ActivityOptions.makeSceneTransitionAnimation(activity,
            Pair.create(this,"fullImage")).toBundle()

        activity.startActivity(Intent(activity as Context, ImageViewingActivity::class.java).apply {
            putExtra("imgUrl",imgUrl)
        }, optionsBundle)

}
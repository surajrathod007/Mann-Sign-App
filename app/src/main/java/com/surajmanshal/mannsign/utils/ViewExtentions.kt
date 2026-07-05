package com.surajmanshal.mannsign.utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Pair
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
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

fun Context.makeACall(number : String){
    startActivity(Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:${number}")
    })
}

/**
 * For a fixed-height AppBarLayout/toolbar sitting at the top of a screen.
 * Captures the XML height, then on each inset pass adds status-bar height as
 * top padding and expands the view's total height to match.
 */
fun View.applyStatusBarInset() {
    val baseHeight = layoutParams.height
    val isFixedHeight = baseHeight > 0  // WRAP_CONTENT=-2, MATCH_PARENT=-1 must not be modified
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
        v.updatePadding(top = insets.top)
        if (isFixedHeight) v.updateLayoutParams { height = baseHeight + insets.top }
        windowInsets
    }
}

/**
 * For any scrollable container (ScrollView, RecyclerView, NestedScrollView).
 * Adds navigation-bar height as bottom padding so the last item is never
 * obscured by the gesture bar or nav buttons.
 */
fun View.applyNavBarInset() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
        v.updatePadding(bottom = insets.bottom)
        windowInsets
    }
}

/**
 * For screens with no toolbar (form-only activities, full-screen dialogs).
 * Applies both status-bar (top) and navigation-bar (bottom) insets to the
 * root scrollable view in a single call.
 */
fun View.applySystemBarInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        val insets = windowInsets.getInsets(
            WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars()
        )
        v.updatePadding(top = insets.top, bottom = insets.bottom)
        windowInsets
    }
}
package com.surajmanshal.mannsign.ui.activity

import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.WindowCompat
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.SecuredScreenActivity
import com.surajmanshal.mannsign.databinding.FragmentViewProfilePicBinding
import com.surajmanshal.mannsign.utils.applyNavBarInset
import com.surajmanshal.mannsign.utils.applyStatusBarInset

class ImageViewingActivity : SecuredScreenActivity() {

    lateinit var binding: FragmentViewProfilePicBinding
    var imgUrl : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentViewProfilePicBinding.inflate(layoutInflater)
        imgUrl = intent.getStringExtra("imgUrl")
        Glide.with(this@ImageViewingActivity).load(imgUrl).error(R.drawable.person_user).into(binding.imgProfilePicFrag)
        setContentView(binding.root)
        // needs light (white) icons on Black background here
        // Scoped to this Activity's own Window, so the previous screen's dark icons are unaffected
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        binding.toolbar.root.applyStatusBarInset()
        binding.root.applyNavBarInset()
        binding.toolbar.ivBackButton.apply {
            imageTintList = AppCompatResources
                .getColorStateList(this@ImageViewingActivity,R.color.white)
            setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

}
package com.surajmanshal.mannsign.ui.activity

import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.SecuredScreenActivity
import com.surajmanshal.mannsign.databinding.FragmentViewProfilePicBinding

class ImageViewingActivity : SecuredScreenActivity() {

    lateinit var binding: FragmentViewProfilePicBinding
    var imgUrl : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentViewProfilePicBinding.inflate(layoutInflater)
        imgUrl = intent.getStringExtra("imgUrl")
        Glide.with(this@ImageViewingActivity).load(imgUrl).error(R.drawable.person_user).into(binding.imgProfilePicFrag)
        setContentView(binding.root)
        binding.toolbar.ivBackButton.apply {
            imageTintList = AppCompatResources
                .getColorStateList(this@ImageViewingActivity,R.color.white)
            setOnClickListener {
                onBackPressed()
            }
        }
    }

}
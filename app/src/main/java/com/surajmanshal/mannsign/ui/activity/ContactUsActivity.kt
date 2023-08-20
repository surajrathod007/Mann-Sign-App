package com.surajmanshal.mannsign.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.surajmanshal.mannsign.databinding.ActivityContactUsBinding
import com.surajmanshal.mannsign.utils.Constants

class ContactUsActivity : AppCompatActivity() {
    lateinit var binding : ActivityContactUsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            tvPhoneNo.text = Constants.MANN_SIGN_PHONE_NUMBER
            tvWhatAppNo.text = Constants.MANN_SIGN_PHONE_NUMBER
            tvEmail.text = Constants.MANN_SIGN_MAIL

            ivWebsite.setOnClickListener {

            }
            ivIG.setOnClickListener {

            }
            ivFB.setOnClickListener {

            }
        }
    }
    fun openLink(url : String){
        startActivity(Intent(Intent.ACTION_VIEW))
    }
}
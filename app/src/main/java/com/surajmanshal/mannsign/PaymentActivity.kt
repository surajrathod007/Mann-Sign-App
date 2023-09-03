package com.surajmanshal.mannsign

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.surajmanshal.mannsign.databinding.ActivityPaymentBinding
import com.surajmanshal.mannsign.ui.fragments.PhonePeFragment

class PaymentActivity : AppCompatActivity() {
    lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer,PhonePeFragment())
            .commit()
    }
}
package com.surajmanshal.mannsign.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.databinding.ActivityOrderPlacedBinding

class OrderPlacedActivity : AppCompatActivity() {

    lateinit var binding : ActivityOrderPlacedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderPlacedBinding.inflate(layoutInflater)

        setupViews()
        setupClickListeners()
        setContentView(binding.root)
    }

    private fun setupViews() {
        Glide.with(this).load(R.drawable.gif_success).into(binding.animationOrderPlaced)
    }

    private fun setupClickListeners() {
        binding.btnViewMyOrders.setOnClickListener {
            startActivity(Intent(this@OrderPlacedActivity,OrdersActivity::class.java))
            finish()
        }
        binding.btnPlacedBack.setOnClickListener {
            finish()
        }
    }
}
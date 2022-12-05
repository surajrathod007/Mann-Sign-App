package com.surajmanshal.mannsign.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.recyclerview.OrdersAdapter
import com.surajmanshal.mannsign.databinding.ActivityOrdersBinding
import com.surajmanshal.mannsign.viewmodel.OrdersViewModel

class OrdersActivity : AppCompatActivity() {

    lateinit var binding : ActivityOrdersBinding
    lateinit var vm : OrdersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vm = ViewModelProvider(this).get(OrdersViewModel::class.java)
        val sharedPreference =  getSharedPreferences("user_e", Context.MODE_PRIVATE)

        val email = sharedPreference.getString("email","")
        if(!email.isNullOrEmpty())
            loadOrders(email)
        setObservers()

        binding.btnOrderBack.setOnClickListener {
            onBackPressed()
            finish()
        }

    }

    private fun loadOrders(email : String){
        vm.getCustomerOrders(email)
    }

    private fun setObservers(){
        vm.customerOrders.observe(this){
            binding.rvOrders.adapter = OrdersAdapter(this@OrdersActivity,it)
        }
    }
}
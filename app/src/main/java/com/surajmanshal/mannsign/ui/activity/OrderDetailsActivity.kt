package com.surajmanshal.mannsign.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.recyclerview.OrderItemsAdapter
import com.surajmanshal.mannsign.databinding.ActivityOrderDetailsBinding
import com.surajmanshal.mannsign.databinding.ActivityOrdersBinding
import com.surajmanshal.mannsign.viewmodel.OrdersViewModel

class OrderDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityOrderDetailsBinding
    lateinit var vm: OrdersViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(OrdersViewModel::class.java)
        setContentView(binding.root)

        val id = intent.getStringExtra("id")
        if (!id.isNullOrEmpty()) {
            vm.getOrderById(id)
        }
        setObservers()

        binding.btnOrderDetailBack.setOnClickListener {
            onBackPressed()
            finish()
        }
    }

    private fun setObservers() {
        vm.order.observe(this) {
            binding.rvOrderItems.adapter = OrderItemsAdapter(this, it.orderItems!!)
            binding.txtOrderIdDetails.text =   it.orderId
            binding.txtOrderDateDetails.text = it.orderDate.toString()
            binding.txtOrderTotalDetails.text = it.total.toString()
            binding.txtEstimatedDays.text = if(it.days == null) "0" else it.days.toString()
            if(!it.trackingUrl.isNullOrEmpty())
                binding.edTrackingUrl.setText(it.trackingUrl.toString())
            binding.txtOrderPaymentStatus.text = if(it.paymentStatus==0) "Pending" else "Done"
            binding.txtOrderQuantityDetails.text = it.quantity.toString()
        }

    }
}
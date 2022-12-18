package com.surajmanshal.mannsign.ui.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.recyclerview.OrderItemsAdapter
import com.surajmanshal.mannsign.databinding.ActivityOrderDetailsBinding
import com.surajmanshal.mannsign.databinding.ActivityOrdersBinding
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.viewmodel.OrdersViewModel
import kotlinx.coroutines.Runnable

class OrderDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityOrderDetailsBinding
    lateinit var vm: OrdersViewModel

    lateinit var mHandler: Handler
    lateinit var mRunnable: Runnable

    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()
        if(mRunnable != null){
            mHandler.removeCallbacks(mRunnable)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(OrdersViewModel::class.java)
        setContentView(binding.root)

        val id = intent.getStringExtra("id")


        window.statusBarColor = Color.BLACK
        //TODO : Every 5 second new request is made
        mHandler = Handler()
        mHandler.post(object : Runnable {
            override fun run() {
                mRunnable = this
                if (!id.isNullOrEmpty()) {
                    vm.getOrderById(id)
                }

                mHandler.postDelayed(this,5000)
            }
        })

        setObservers()

        binding.btnOrderDetailBack.setOnClickListener {
            onBackPressed()
            finish()
        }


    }

    private fun setObservers() {
        vm.order.observe(this) {
            binding.rvOrderItems.adapter = OrderItemsAdapter(this, it.orderItems!!)
            binding.txtOrderIdDetails.text = it.orderId
            binding.txtOrderDateDetails.text = it.orderDate.toString()
            binding.txtOrderTotalDetails.text = it.total.toString()
            binding.txtEstimatedDays.text = if (it.days == null) "0" else it.days.toString()
            if (!it.trackingUrl.isNullOrEmpty())
                binding.edTrackingUrl.setText(it.trackingUrl.toString())
            binding.txtOrderPaymentStatus.text = if (it.paymentStatus == 0) "Pending" else "Done"
            binding.txtOrderQuantityDetails.text = it.quantity.toString()
            binding.txtOrderDiscountDetails.text = "- $" + it.discount
            binding.txtOrderDeliveryDetails.text = "+ $" + it.deliveryCharge
            binding.txtOrderGrandTotalDetails.text = "$" + it.totalRecieved
            if(it.paymentStatus == 0)
                binding.txtYouHaveToPay.text = "You have to pay : "
            else
                binding.txtYouHaveToPay.text = "You paid : "


            if(it.orderStatus == Constants.ORDER_CONFIRMED)
                binding.btnMakePayment.visibility = View.VISIBLE
            else
                binding.btnMakePayment.visibility = View.GONE

            when (it.orderStatus) {
                Constants.ORDER_PENDING -> {
                    binding.txtOrderDetailsStatus.text = "Pending"
                }
                Constants.ORDER_CONFIRMED -> {
                    binding.txtOrderDetailsStatus.text = "Confirmed"
                }
                Constants.ORDER_PROCCESSING -> {
                    binding.txtOrderDetailsStatus.text = "Processing"
                }
                Constants.ORDER_READY -> {
                    binding.txtOrderDetailsStatus.text = "Ready"
                }
                Constants.ORDER_DELIVERED -> {
                    binding.txtOrderDetailsStatus.text = "Delivered"
                }
                Constants.ORDER_CANCELED -> {
                    binding.txtOrderDetailsStatus.text = "Canceled"
                }
            }
        }

    }
}
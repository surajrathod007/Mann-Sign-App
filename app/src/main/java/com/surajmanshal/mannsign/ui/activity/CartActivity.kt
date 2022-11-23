package com.surajmanshal.mannsign.ui.activity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.recyclerview.CartItemAdapter
import com.surajmanshal.mannsign.databinding.ActivityCartBinding
import com.surajmanshal.mannsign.viewmodel.CartViewModel

class CartActivity : AppCompatActivity() {

    lateinit var binding: ActivityCartBinding
    lateinit var vm: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCartBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(CartViewModel::class.java)

        loadCarts("surajsinhrathod75@gmail.com")
        setContentView(binding.root)

        binding.sRefresh.setOnRefreshListener {
            loadCarts("surajsinhrathod75@gmail.com")
        }
        vm.msg.observe(this) {
            Toast.makeText(this@CartActivity, it.toString(), Toast.LENGTH_LONG).show()
        }
        vm.cartItems.observe(this) {
            if (it.isNullOrEmpty()) {
                Toast.makeText(this, "No Cart Items ;(", Toast.LENGTH_SHORT).show()
                binding.btnPlaceOrder.isEnabled = false
            }else{
                binding.btnPlaceOrder.isEnabled = true
            }
            binding.rvCartItems.adapter = CartItemAdapter(this@CartActivity, it, vm)
            if (binding.sRefresh.isRefreshing)
                binding.sRefresh.isRefreshing = false
            var t = 0f
            it.forEach {
                t += it.totalPrice
            }
            vm._total.postValue(t)
            val to = t - vm._discount.value!!
            vm._amountToPay.postValue(to + vm._delivery.value!!)
        }

        vm._areas.observe(this) {
            val c = it.random().minCharge
            vm._delivery.postValue(c)
        }
        vm._total.observe(this) {
            binding.txtSubTotal.text = "$" + it.toString()
        }
        vm._discount.observe(this) {
            binding.txtDiscount.text = "-$" + it.toString()
        }
        vm._amountToPay.observe(this) {
            binding.txtAmountToPay.text = "$" + it.toString()
        }
        vm._delivery.observe(this) {
            binding.txtDeliveryCharge.text = "+$" + it.toString()
        }
        vm.isLoading.observe(this) {
            if (it) {
                binding.progressCart.visibility = View.VISIBLE
            } else {
                binding.progressCart.visibility = View.GONE
            }
        }
        vm.orderPlaced.observe(this) {
            if (it) {
                loadCarts("surajsinhrathod75@gmail.com")
                vm.clearValues()
                val b = AlertDialog.Builder(this)
                b.setTitle("Your order is placed !")
                b.setMessage("Thanks you for ordering from mann sign ;)")
                b.show()
            }
        }
        //buttons
        binding.btnPlaceOrder.setOnClickListener {
            val b = AlertDialog.Builder(this)
            b.setTitle("Confirm your order please")
            b.setMessage("Subtotal : $${vm._total.value}\nDiscount : $${vm._discount.value}\nDelivery : $${vm._delivery.value}\nYou have to pay : $${vm._amountToPay.value}")
            b.setPositiveButton("Pay") { v, m ->
                vm.placeOrder()
            }
            b.setNegativeButton("Cancel") { v, m ->
                Toast.makeText(this, "Order canceled", Toast.LENGTH_LONG).show()
                v.dismiss()
            }
            b.show()

        }
        binding.btnApplyCoupon.setOnClickListener {
            vm.useCoupon(binding.edCouponCode.text.toString())
        }
    }


    private fun loadCarts(email: String) {
        vm.getCartItems(email)
    }

}
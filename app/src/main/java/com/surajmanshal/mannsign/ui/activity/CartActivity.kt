package com.surajmanshal.mannsign.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

        vm.msg.observe(this) {
            Toast.makeText(this@CartActivity, it.toString(), Toast.LENGTH_LONG).show()
        }
        vm.cartItems.observe(this){
            binding.rvCartItems.adapter = CartItemAdapter(this@CartActivity,it,vm)
            var t = 0f
            it.forEach {
                t+=it.totalPrice
            }
            vm._total.postValue(t)
            vm._amountToPay.postValue(t- vm._discount.value!!)
        }

        vm._total.observe(this){
            binding.txtSubTotal.text = "$" + it.toString()
        }
        vm._discount.observe(this){
            binding.txtDiscount.text = "-$" + it.toString()
        }
        vm._amountToPay.observe(this){
            binding.txtAmountToPay.text = "$" + it.toString()
        }
        //buttons
        binding.btnPlaceOrder.setOnClickListener {
            vm.doCalculation()
        }
        binding.btnApplyCoupon.setOnClickListener {
            vm.useCoupon(binding.edCouponCode.text.toString())
        }
    }




    private fun loadCarts(email: String) {
        vm.getCartItems(email)
    }
}
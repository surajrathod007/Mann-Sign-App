package com.surajmanshal.mannsign.ui.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal
import com.surajmanshal.mannsign.AuthenticationActivity
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.recyclerview.CartItemAdapter
import com.surajmanshal.mannsign.databinding.ActivityCartBinding
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.viewmodel.CartViewModel

class CartActivity : AppCompatActivity() {

    lateinit var binding: ActivityCartBinding
    lateinit var vm: CartViewModel

    var email: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCartBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(CartViewModel::class.java)

        window.statusBarColor = Color.BLACK
        val sharedPreference = getSharedPreferences("user_e", Context.MODE_PRIVATE)
        email = sharedPreference.getString("email", "")

        binding.shimmerCartLoading.startShimmer()
        if (!email.isNullOrEmpty()) {
            loadCarts(email!!)
        }

        setContentView(binding.root)


        binding.btnCartBack.setOnClickListener {
            onBackPressed()
            finish()
        }

        binding.emptyCartView.txtEmptyMessage.text = "Cart is empty !"

        binding.btnLoginRegisterCart.setOnClickListener {
            startActivity(Intent(this, AuthenticationActivity::class.java))
            finish()
        }

        binding.sRefresh.setOnRefreshListener {
            loadCarts(email!!)
        }
        vm.msg.observe(this) {
            Toast.makeText(this@CartActivity, it.toString(), Toast.LENGTH_LONG).show()
        }
        vm.cartItems.observe(this) {
            if (it.isNullOrEmpty()) {
                binding.btnPlaceOrder.isEnabled = false
                //binding.sCartNested.visibility = View.GONE
                binding.emptyCartView.root.visibility = View.VISIBLE
                binding.shimmerCartLoading.visibility = View.GONE
                binding.sCartNested.visibility = View.GONE


            } else {
                //Functions.makeToast(this@CartActivity,"${it.size}")
                Handler().postDelayed({
                    binding.shimmerCartLoading.visibility = View.GONE
                    binding.sCartNested.visibility = View.VISIBLE
                    binding.btnPlaceOrder.isEnabled = true
                }, 1500)


                binding.emptyCartView.root.visibility = View.GONE
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
            binding.txtSubTotal.text = "₹" + it.toString()
        }
        vm._discount.observe(this) {
            binding.txtDiscount.text = "-₹" + it.toString()
        }
        vm._amountToPay.observe(this) {
            binding.txtAmountToPay.text = "₹" + it.toString()
        }
        vm._delivery.observe(this) {
            binding.txtDeliveryCharge.text = "+₹" + it.toString()
        }
        vm.isLoading.observe(this) {
            if (!email.isNullOrEmpty()) {
                if (it) {
                    binding.shimmerCartLoading.visibility = View.VISIBLE
                    binding.sCartNested.visibility = View.GONE
                    binding.emptyCartView.root.visibility = View.GONE
                    //binding.emptyCartView.root.visibility = View.GONE
                } else {
                    Handler().postDelayed({
                        binding.shimmerCartLoading.visibility = View.GONE
                        //binding.emptyCartView.root.visibility = View.GONE
                        //binding.sCartNested.visibility = View.VISIBLE
                        if(vm.showScroll.value!!)
                            binding.sCartNested.visibility = View.VISIBLE
                    }, 1500)
                }
            } else {
                binding.emptyCartView.root.visibility = View.GONE
                binding.shimmerCartLoading.visibility = View.GONE
                binding.sCartNested.visibility = View.GONE
                binding.sRefresh.visibility = View.GONE
                binding.cartLogin.visibility = View.VISIBLE
            }

        }
        vm.orderPlaced.observe(this) {
            if (it) {
                loadCarts(email!!)
                vm.clearValues()
                vm.setScrollVisibility(false)
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
            b.setMessage("Subtotal : ₹${vm._total.value}\nDiscount : ₹${vm._discount.value}\nDelivery : ₹${vm._delivery.value}\nYou have to pay : ₹${vm._amountToPay.value}")
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
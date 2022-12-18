package com.surajmanshal.mannsign.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.surajmanshal.mannsign.AuthenticationActivity
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.recyclerview.OrdersAdapter
import com.surajmanshal.mannsign.databinding.ActivityOrdersBinding
import com.surajmanshal.mannsign.viewmodel.OrdersViewModel

class OrdersActivity : AppCompatActivity() {

    lateinit var binding : ActivityOrdersBinding
    lateinit var vm : OrdersViewModel
    var email : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vm = ViewModelProvider(this).get(OrdersViewModel::class.java)
        val sharedPreference =  getSharedPreferences("user_e", Context.MODE_PRIVATE)

        binding.shimmerOrderLoading.startShimmer()
        email = sharedPreference.getString("email","")
        if(!email.isNullOrEmpty())
            loadOrders(email!!)

        window.statusBarColor = Color.BLACK

        setObservers()

        binding.swipeRefreshOrder.setOnRefreshListener {
            if(!email.isNullOrEmpty())
                loadOrders(email!!)
            else
                binding.swipeRefreshOrder.isRefreshing = false
        }
        binding.emptyOrderView.txtEmptyMessage.text = "No orders !"
        binding.btnOrderBack.setOnClickListener {
            onBackPressed()
            finish()
        }
        binding.loginRegisterOrder.btnLoginRegister.setOnClickListener {
            startActivity(Intent(this, AuthenticationActivity::class.java))
            finish()
        }

    }

    private fun loadOrders(email : String){
        vm.getCustomerOrders(email)
    }

    private fun setObservers(){
        vm.customerOrders.observe(this){
            if(it.isNullOrEmpty()){
                binding.shimmerOrderLoading.visibility = View.GONE
                binding.rvOrders.visibility = View.GONE
                //binding.bounceScroll.visibility = View.GONE
                binding.emptyOrderView.root.visibility = View.VISIBLE
            }
            binding.rvOrders.adapter = OrdersAdapter(this@OrdersActivity,it)
        }
        vm.isLoading.observe(this){
            if(!email.isNullOrEmpty())
            {
                if(it){
                    binding.shimmerOrderLoading.visibility = View.VISIBLE
                    binding.rvOrders.visibility = View.GONE
                    binding.emptyOrderView.root.visibility = View.GONE
                }else{
                    Handler().postDelayed({
                        binding.shimmerOrderLoading.visibility = View.GONE
                        binding.rvOrders.visibility = View.VISIBLE
                        binding.bounceScroll.visibility = View.VISIBLE
                        binding.swipeRefreshOrder.isRefreshing = false
                    },1500)

                }
            }else{
                binding.emptyOrderView.root.visibility = View.GONE
                binding.rvOrders.visibility = View.GONE
                binding.shimmerOrderLoading.visibility = View.GONE
                binding.bounceScroll.visibility = View.GONE
                binding.swipeRefreshOrder.visibility = View.GONE
                binding.loginRegisterOrder.root.visibility = View.VISIBLE
                binding.swipeRefreshOrder.isRefreshing = false
            }

        }
    }
}
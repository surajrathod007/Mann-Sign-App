package com.surajmanshal.mannsign.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.recyclerview.ProductAdapter
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.databinding.ActivityProductCategoryDetailsBinding
import com.surajmanshal.mannsign.viewmodel.ProductCategoryDetailsViewModel

class ProductCategoryDetailsActivity : AppCompatActivity() {

    lateinit var binding : ActivityProductCategoryDetailsBinding
    lateinit var vm : ProductCategoryDetailsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductCategoryDetailsBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(ProductCategoryDetailsViewModel::class.java)
        val id = intent.getIntExtra("sub",0)

        loadData(id)
        setContentView(binding.root)
        setupObserver()
    }

    fun loadData(id : Int){
        vm.loadProductByCat(id)
    }

    fun setupObserver(){
        vm.products.observe(this){
            binding.rvProductCatDetails.layoutManager = GridLayoutManager(this,2)
            binding.rvProductCatDetails.adapter = ProductAdapter(this@ProductCategoryDetailsActivity,it)
        }
    }
}
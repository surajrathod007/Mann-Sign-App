package com.surajmanshal.mannsign.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.surajmanshal.mannsign.adapter.recyclerview.ProductAdapter
import com.surajmanshal.mannsign.databinding.ActivityProductCategoryDetailsBinding
import com.surajmanshal.mannsign.viewmodel.ProductCategoryDetailsViewModel

class ProductCategoryDetailsActivity : AppCompatActivity() {

    lateinit var binding : ActivityProductCategoryDetailsBinding
    lateinit var vm : ProductCategoryDetailsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductCategoryDetailsBinding.inflate(layoutInflater)
        vm = ViewModelProvider(this).get(ProductCategoryDetailsViewModel::class.java)
        val id = intent.getIntExtra("sub",1)
        val name = intent.getStringExtra("name")
        binding.editProductCategorySearch.setText(name)


        setContentView(binding.root)
        binding.shimmerSearchLoading.startShimmer()
        loadData(id,name)

        search()
        setupObserver()




        binding.btnProductCatBack.setOnClickListener {
            finish()
        }
    }

    fun loadData(id: Int, name: String?){
        vm.loadProductByCat(id,name){

        }
    }

    fun setupObserver(){
        vm.filteredProducts.observe(this){
            binding.rvProductCatDetails.layoutManager = GridLayoutManager(this,2)
            binding.rvProductCatDetails.adapter = ProductAdapter(this@ProductCategoryDetailsActivity,it)
        }
        vm.isLoading.observe(this){
            if(it){
                binding.shimmerSearchLoading.visibility = View.VISIBLE
                binding.rvProductCatDetails.visibility = View.GONE
            }else{
                Handler().postDelayed({
                    binding.shimmerSearchLoading.visibility = View.GONE
                    binding.rvProductCatDetails.visibility = View.VISIBLE
                },1000)
            }
        }
    }

    fun search(){
        binding.editProductCategorySearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //vm.searchProduct(text.toString())
            }
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                vm.searchProduct(text.toString())
            }
            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }
}
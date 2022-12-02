package com.surajmanshal.mannsign.ui.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.databinding.ActivityProductDetailsBinding
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.Functions.urlMaker
import com.surajmanshal.mannsign.viewmodel.ProductsViewModel

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProductDetailsBinding
    private lateinit var vm : ProductsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vm = ViewModelProvider(this)[ProductsViewModel::class.java]
        val owner = this

        with(vm){
            _currentProduct.value = intent.getSerializableExtra(Constants.PRODUCT) as Product?
            _currentProductCategory.observe(owner, Observer {
                setupCategoryView(it.name)
            })
            _currentProductSubCategory.observe(owner, Observer {
                setupSubCategoryView(it.name)
            })
            _currentProductMaterial.observe(owner, Observer { materials->
                mutableListOf<String>().apply {
                    materials?.forEach {
                        add(it.name)
                        if(size== materials.size) setupSpinner(binding.materialSpinner,this)
                    }
                }
            })
            _currentProductLanguage.observe(owner, Observer { languages ->
                mutableListOf<String>().apply {
                    languages?.forEach {
                        add(it.name)
                        if(size== languages.size) setupSpinner(binding.languageSpinner,this)
                    }
                }
            })
            _currentProduct.value?.let { product->
                with(product){
                    materials?.forEach { materialId -> getMaterialById(materialId) }
                    languages?.forEach { languageId -> getLanguageById(languageId) }
                    category?.let { categoryId -> getCategoryById(categoryId) }
                    subCategory?.let { subCategoryId -> getSubCategoryById(subCategoryId) }
                }
                with(binding){
                    Glide.with(this@ProductDetailsActivity)
                        .load(urlMaker(product.images!![0].url)).into(ivProduct)
                    mutableListOf<String>().apply {
                        product.sizes?.forEach {
                            add("${it.width} x ${it.height}")
                            if(size== product.sizes!!.size) setupSpinner(sizeSpinner,this)
                        }
                    }
                    tvBasePrice.text = "${tvBasePrice.text} ${product.basePrice}"
                }
            }
        }
    }

    private fun setupCategoryView(name: String) {
        binding.tvCategory.text = binding.tvCategory.text.toString() + name
    }

    private fun setupSubCategoryView(name: String) {
        binding.tvSubCategory.text = binding.tvSubCategory.text.toString() + name
    }

    fun setupSpinner(spinner: Spinner,dataList : List<String>){
        spinner.adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,dataList)
    }
}
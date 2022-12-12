package com.surajmanshal.mannsign.ui.activity

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.adapter.recyclerview.ReviewAdapter
import com.surajmanshal.mannsign.data.model.Review
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.databinding.ActivityProductDetailsBinding
import com.surajmanshal.mannsign.room.UserDatabase
import com.surajmanshal.mannsign.room.UserEntity
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.utils.Functions.urlMaker
import com.surajmanshal.mannsign.viewmodel.ProductsViewModel

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProductDetailsBinding
    private lateinit var vm : ProductsViewModel
    private var currentUser : UserEntity? = null

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
                        if(isNotEmpty()) binding.materialSpinner.resSpinner.setText(get(0))
                        if(size== materials.size) setupSpinner(binding.materialSpinner.resSpinner,this)
                    }
                    binding.materialSpinner.resSpinnerContainer.hint = "Material"
                }
            })
            _currentProductLanguage.observe(owner, Observer { languages ->
                mutableListOf<String>().apply {
                    languages?.forEach {
                        add(it.name)
                        if(isNotEmpty()) binding.languageSpinner.resSpinner.setText(get(0))
                        if(size== languages.size) setupSpinner(binding.languageSpinner.resSpinner,this)
                    }
                    binding.languageSpinner.resSpinnerContainer.hint = "Language"
                }
            })
            _currentProductReviews.observe(owner){
                binding.tvNoReviews.isVisible = it.isEmpty()
                binding.rvProductReviews.isVisible = it.isNotEmpty()
                setupProductReviews(it)
            }
            _currentProduct.value?.let { product->
                with(product){
                    materials?.forEach { materialId -> getMaterialById(materialId) }
                    languages?.forEach { languageId -> getLanguageById(languageId) }
                    category?.let { categoryId -> getCategoryById(categoryId) }
                    subCategory?.let { subCategoryId -> getSubCategoryById(subCategoryId) }
                    fetchProductReview(productId)
                }
                with(binding){
                    Glide.with(this@ProductDetailsActivity)
                        .load(urlMaker(product.images!![0].url)).into(ivProduct)
                    mutableListOf<String>().apply {
                        product.sizes?.forEach {
                            add("${it.width} x ${it.height}")
                            if(isNotEmpty()) sizeSpinner.resSpinner.setText(get(0))
                            if(size== product.sizes!!.size) setupSpinner(sizeSpinner.resSpinner,this)
                        }
                        sizeSpinner.resSpinnerContainer.hint = "Size"
                    }

                    product.posterDetails?.let {
                        binding.tvTitle.text = title
                        if(it.long_desc!!.length > 64){
                            Functions.addReadMore(
                                it.long_desc,
                                binding.tvProductDescriptionLong,
                                binding.tvProductDescriptionShort
                            )
                        }else{
                            binding.tvProductDescriptionShort.text = product.posterDetails!!.long_desc!!
                        }
                    }


                    tvBasePrice.text = "${tvBasePrice.text} ${product.basePrice}"

                }
            }
        }
        val sharedPreferences = getSharedPreferences("user_e", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email","no email")
        val db = UserDatabase.getDatabase(this).userDao()

        val user = db.getUser(email!!)
        user.observe(this){
            currentUser = it
            setupProductReviews(emptyList())
        }
        binding.rvProductReviews.layoutManager = LinearLayoutManager(this,OrientationHelper.HORIZONTAL,false)

    }

    private fun setupCategoryView(name: String) {
        binding.tvCategory.text = binding.tvCategory.text.toString() + name
    }

    private fun setupSubCategoryView(name: String) {
        binding.tvSubCategory.text = binding.tvSubCategory.text.toString() + name
    }

    fun setupSpinner(spinner: AutoCompleteTextView, dataList: List<String>){
        spinner.setAdapter(ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,dataList))
    }

    fun setupProductReviews(reviews : List<Review>){
        currentUser?.let {
            binding.rvProductReviews.adapter = ReviewAdapter(this,reviews,null,currentUser)
        }
    }

}
package com.surajmanshal.mannsign.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.adapter.recyclerview.ReviewAdapter
import com.surajmanshal.mannsign.data.model.Review
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.databinding.ActivityProductDetailsBinding
import com.surajmanshal.mannsign.room.UserDatabase
import com.surajmanshal.mannsign.room.UserEntity
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.utils.Functions.urlMaker
import com.surajmanshal.mannsign.viewmodel.CartViewModel
import com.surajmanshal.mannsign.viewmodel.ProductsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProductDetailsBinding
    private lateinit var vm : ProductsViewModel
    private lateinit var cartVm : CartViewModel
    private var currentUser : UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vm = ViewModelProvider(this)[ProductsViewModel::class.java]
        cartVm = ViewModelProvider(this)[CartViewModel::class.java]

        val owner = this
        val sharedPreferences = getSharedPreferences("user_e", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email","no email")

        with(cartVm){

            // Observers ----------------------------------------------------------------------------------

            _selectedVariant.observe(owner){
                with(binding.productBuyingLayout){
                    tvVariantPrice.text = resources.getString(com.surajmanshal.mannsign.R.string.selected_variant_price)+it.variantPrice
                    tvAmount.text = "${it.variantPrice?.times(evQty.text.toString().toInt())}"
                }
            }

            _selectedSize.observe(owner){
                calculateVariantPrice()
            }
            _selectedMaterial.observe(owner){
                calculateVariantPrice()
            }

            cartVm.serverResponse.observe(owner){
                Toast.makeText(owner, it.message, Toast.LENGTH_SHORT).show()
                if(it.success) setupGoToCart()
            }

        }
        with(vm){
            _currentProduct.value = intent.getSerializableExtra(Constants.PRODUCT) as Product?

            // Observers ----------------------------------------------------------------------------------
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
                        if(isNotEmpty())
                            binding.materialSpinner.resSpinner.setText(get(0))
                        if(size== materials.size){
                            cartVm._selectedMaterial.value = materials[0]
                            setupSpinner(binding.materialSpinner.resSpinner,this)
                        }
                    }
                    binding.materialSpinner.resSpinnerContainer.hint = "Material"
                }
            })
            _currentProductLanguage.observe(owner, Observer { languages ->
                mutableListOf<String>().apply {
                    languages?.forEach {
                        add(it.name)
                        if(isNotEmpty()) binding.languageSpinner.resSpinner.setText(get(0))
                        if(size== languages.size){
                            cartVm._selectedLanguage.value = languages[0]
                            setupSpinner(binding.languageSpinner.resSpinner,this)
                        }
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
                    cartVm._selectedVariant.value?.apply {
                        this.productId = product.productId
                        sizeId = sizes?.get(0)?.sid
                        materialId = materials?.get(0)
                        languageId = languages?.get(0)
                        cartVm.refreshVariant()
                    }
                    materials?.forEach { materialId -> getMaterialById(materialId) }
                    languages?.forEach { languageId -> getLanguageById(languageId) }
                    category?.let { categoryId -> getCategoryById(categoryId) }
                    subCategory?.let { subCategoryId -> getSubCategoryById(subCategoryId) }
                    fetchProductReview(productId)
                }
                with(binding){
                    if(product.images?.isNotEmpty() == true)
                    Glide.with(this@ProductDetailsActivity)
                        .load(urlMaker(product.images!![0].url)).into(ivProduct)
                    mutableListOf<String>().apply {
                        product.sizes?.forEach {
                            add("${it.width} x ${it.height}")
                            if(isNotEmpty()) sizeSpinner.resSpinner.setText(get(0))
                            if(size== product.sizes!!.size){
                                cartVm._selectedSize.value = product.sizes!![0]
                                setupSpinner(sizeSpinner.resSpinner,this)
                            }
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

                    // Click Listeners ----------------------------------------------------------------------------------
                    productBuyingLayout.apply {
                        btnAddVariantToCart.setOnClickListener {
                            CoroutineScope(Dispatchers.IO).launch {
                                cartVm.addToCart(
                                    email!!,
                                    cartVm._selectedVariant.value!!,
                                    if(binding.productBuyingLayout.evQty.text.toString()=="") 1 else
                                        binding.productBuyingLayout.evQty.text.toString().toInt()
                                )
                            }
                        }
                    }
                    sizeSpinner.resSpinner.setOnItemClickListener { adapterView, view, index, l ->
                        with(cartVm){
                            _selectedVariant.value?.apply {
                            _currentProduct.value?.let {
                                sizeId = it.sizes?.get(index)?.sid
                                _selectedSize.value = it.sizes?.get(index)
                            }
                        }}
                    }
                    materialSpinner.resSpinner.setOnItemClickListener { adapterView, view, index, l ->
                        with(cartVm) {
                            _selectedVariant.value?.apply {
                                _currentProduct.value?.let {
                                    materialId = it.materials?.get(index)
                                    _selectedMaterial.value = vm._currentProductMaterial.value?.get(index)
                                }
                            }
                        }
                    }
                    languageSpinner.resSpinner.setOnItemClickListener { adapterView, view, index, l ->
                        with(cartVm){
                            _selectedVariant.value?.apply {
                                _currentProduct.value?.let {
                                    languageId = it.languages?.get(index)
                                    _selectedLanguage.value = vm._currentProductLanguage.value?.get(index)
                                }
                            }
                        }
                    }
                    // Text changed Listeners ----------------------------------------------------------------------------------
                    with(productBuyingLayout){
                        evQty.setText("1")
                        evQty.doOnTextChanged{ text,start,before,count ->
                            fun invalidQtyHandler(){
                                evQty.setText("1")
                                tvAmount.text = "${cartVm._selectedVariant.value?.variantPrice?.times(1)}"
                                Toast.makeText(this@ProductDetailsActivity, "Minimum Quantity is 1", Toast.LENGTH_SHORT).show()
                            }
                            if(!text.isNullOrBlank()){
                                if(text.toString()=="0"){
                                   invalidQtyHandler()
                                }else{
                                    tvAmount.text = "${cartVm._selectedVariant.value?.variantPrice?.times(
                                        text.toString().toInt()
                                    )}"
                                }
                            }
                        }
                    }
                }
            }
        }

        val db = UserDatabase.getDatabase(this).userDao()
        val user = db.getUser(email!!)
        user.observe(this){
            currentUser = it
            setupProductReviews(emptyList())
        }
        binding.rvProductReviews.layoutManager = LinearLayoutManager(this,OrientationHelper.HORIZONTAL,false)

    }

    private fun setupGoToCart(){
        with(binding.productBuyingLayout){
            btnAddVariantToCart.apply {
                backgroundTintList = resources.getColorStateList(R.color.buttonColor)
                text = context.getString(R.string.go_to_cart)
                setOnClickListener { startActivity(Intent(this@ProductDetailsActivity,CartActivity::class.java)) }
            }
        }
    }

    private fun calculateVariantPrice(){
        with(binding){
            with(cartVm){
                val areaOfSize = _selectedSize.value?.let {
                    return@let it.width*it.height
                }
                val materialPrice = _selectedMaterial.value?.price
                if (areaOfSize != null && materialPrice!=null) {
                    setVariantPrice(areaOfSize* materialPrice)
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

    fun setupSpinner(spinner: AutoCompleteTextView, dataList: List<String>){
        spinner.setAdapter(ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,dataList))
    }

    fun setupProductReviews(reviews : List<Review>){
        currentUser?.let {
            binding.rvProductReviews.adapter = ReviewAdapter(this,reviews,null,currentUser)
        }
    }

}
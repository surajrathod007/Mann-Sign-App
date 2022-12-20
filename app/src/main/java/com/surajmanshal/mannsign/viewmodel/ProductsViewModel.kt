package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsign.data.model.*
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.repository.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductsViewModel : ViewModel() {
    private val repository = Repository()

    // -------------- LIVE DATA -------------------------------------------
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products
    val _currentProduct = MutableLiveData<Product>()
    val _currentProductCategory = MutableLiveData<Category>()
    val _currentProductSubCategory = MutableLiveData<SubCategory>()
    val _currentProductMaterial = MutableLiveData<MutableList<Material>>(mutableListOf())
    val _currentProductLanguage = MutableLiveData<MutableList<Language>>(mutableListOf())
    val _currentProductReviews = MutableLiveData<List<Review>>()
//    val currentProduct : LiveData<Product> get() = _currentProduct

    fun getPosters() {
        val response = repository.fetchPosters()
        response.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                response.body()?.let { _products.value = it}
            }
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                println(t.toString())
            }
        })
    }

    fun getCategoryById(id : Int){
        val response = repository.getCategoryById(id)
        response.enqueue(object : Callback<Category>{
            override fun onResponse(call: Call<Category>, response: Response<Category>) {
                println(response.body())
                response.body()?.let { _currentProductCategory.value = it }
            }

            override fun onFailure(call: Call<Category>, t: Throwable) {
//                TODO("Not yet implemented")
            }

        })
    }

    fun getSubCategoryById(id : Int){
        val response = repository.getSubCategoryById(id)
        response.enqueue(object : Callback<SubCategory>{
            override fun onResponse(call: Call<SubCategory>, response: Response<SubCategory>) {
                println(response.body())
                response.body()?.let { _currentProductSubCategory.value = it }
            }
            override fun onFailure(call: Call<SubCategory>, t: Throwable) {
//                TODO("Not yet implemented")
            }
        })
    }

    fun getMaterialById(id : Int){
        val response = repository.getMaterialById(id)
        response.enqueue(object : Callback<Material>{
            override fun onResponse(call: Call<Material>, response: Response<Material>) {
                println(response.body())
                response.body()?.let {
                    _currentProductMaterial.value?.add(it)
                    _currentProductMaterial.postValue(_currentProductMaterial.value?.sortedBy { it.id } as MutableList<Material>?)
                }
            }
            override fun onFailure(call: Call<Material>, t: Throwable) {
//                TODO("Not yet implemented")
            }
        })
    }

    fun getLanguageById(id : Int){
        val response = repository.getLanguageById(id)
        response.enqueue(object : Callback<Language>{
            override fun onResponse(call: Call<Language>, response: Response<Language>) {
                println(response.body())
                response.body()?.let {
                    _currentProductLanguage.value?.add(it)
                    _currentProductLanguage.postValue(_currentProductLanguage.value?.sortedBy { it.id } as MutableList<Language>?)
                }
            }
            override fun onFailure(call: Call<Language>, t: Throwable) {
//                TODO("Not yet implemented")
            }
        })
    }

    fun fetchProductReview(productId : Int){
        val response = repository.getReview(productId.toString())
        response.enqueue(object : Callback<List<Review>> {
            override fun onResponse(call: Call<List<Review>>, response: Response<List<Review>>) {
                _currentProductReviews.postValue(response.body())
            }
            override fun onFailure(call: Call<List<Review>?>, t: Throwable) {
                println("Failed to fetch product reviews : $t")
            }
        })
    }

}
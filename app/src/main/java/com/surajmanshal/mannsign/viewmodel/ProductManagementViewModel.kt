package com.surajmanshal.mannsignadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsignadmin.data.model.*
import com.surajmanshal.mannsignadmin.data.model.product.Product
import com.surajmanshal.mannsignadmin.repository.Repository
import com.surajmanshal.response.SimpleResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody.Part
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductManagementViewModel : ViewModel() {

    private val repository = Repository()
    // -------------- LIVE DATA -------------------------------------------
    private val _sizes = MutableLiveData<List<Size>>()
    val sizes : LiveData<List<Size>> get() = _sizes                                        //SIZE
    private val _materials = MutableLiveData<List<Material>>()
    val materials : LiveData<List<Material>> get() = _materials                         //MATERIALS
    private val _languages = MutableLiveData<List<Language>>()
    val languages  : LiveData<List<Language>> get() = _languages                        // LANGUAGES
    private val _subCategories = MutableLiveData<List<SubCategory>>()
    val subCategories: LiveData<List<SubCategory>> get() = _subCategories               //CATEGORIES
    private val _serverResponse = MutableLiveData<SimpleResponse>()
    val serverResponse : LiveData<SimpleResponse> get() = _serverResponse               //SERVER RESPONSE
    private val _imageUploadResponse = MutableLiveData<SimpleResponse>()
    val imageUploadResponse : LiveData<SimpleResponse> get() = _imageUploadResponse     // IMAGE UPLOADING PROGRESS
    private val _productUploadResponse = MutableLiveData<SimpleResponse>()
    val productUploadResponse : LiveData<SimpleResponse> get() = _productUploadResponse  // PRODUCT UPLOADING PROGRESS
    private val _posters = MutableLiveData<List<Product>>()
    val posters : LiveData<List<Product>> get() = _posters                              // POSTERS


    // -------------- DATA SETUP FUNCTIONS -------------------------------------------
    suspend fun setupViewModelDataMembers(){
        CoroutineScope(Dispatchers.IO).launch { getSizes() }
        CoroutineScope(Dispatchers.IO).launch { getMaterials() }
        CoroutineScope(Dispatchers.IO).launch { getLanguages() }
        CoroutineScope(Dispatchers.IO).launch { getSubCategories() }
        CoroutineScope(Dispatchers.IO).launch { getPosters() }
    }

    private suspend fun getSizes(){
        val response = repository.fetchSizes()
        println("Response is $response")
        response.enqueue(object : Callback<List<Size>> {
            override fun onResponse(call: Call<List<Size>>, response: Response<List<Size>>) {
                println("Inner Response is $response")
                response.body()?.let { _sizes.value = it }
            }
            override fun onFailure(call: Call<List<Size>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }

    private suspend fun getMaterials(){
        val response = repository.fetchMaterials()
        println("Response is $response")
        response.enqueue(object : Callback<List<Material>> {
            override fun onResponse(call: Call<List<Material>>, response: Response<List<Material>>) {
                println("Inner Response is $response")
                response.body()?.let { _materials.value = it }
            }
            override fun onFailure(call: Call<List<Material>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }

    private suspend fun getLanguages(){
        val response = repository.fetchLanguages()
        println("Response is $response")
        response.enqueue(object : Callback<List<Language>> {
            override fun onResponse(call: Call<List<Language>>, response: Response<List<Language>>) {
                println("Inner Response is $response")
                response.body()?.let { _languages.value = it }
            }
            override fun onFailure(call: Call<List<Language>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }

    private suspend fun getSubCategories(){
        val response = repository.fetchSubCategories()
        println("Response is $response")
        response.enqueue(object : Callback<List<SubCategory>> {
            override fun onResponse(call: Call<List<SubCategory>>, response: Response<List<SubCategory>>) {
                println("Inner Response is $response")
                response.body()?.let { _subCategories.value = it }
            }
            override fun onFailure(call: Call<List<SubCategory>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }

    suspend fun  addProduct(product: Product) {
        try {
            val response = repository.sendProduct(product)
            _serverResponse.postValue(response)
            _productUploadResponse.postValue(response)
        }catch (e : Exception){
            println("$e")
        }
    }

    suspend fun getPosters(){
        val response = repository.fetchPosters()
        println("Response is $response")
        response.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                println("Inner Response is $response")
                response.body()?.let { _posters.value = it }
            }
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }
    suspend fun sendImage(part: Part){
        try {
            val response = repository.uploadImage(part)
            _serverResponse.postValue(response)
            _imageUploadResponse.postValue(response)
        }catch (e : Exception){
            println("$e ${serverResponse.value?.message}")
        }
    }
}
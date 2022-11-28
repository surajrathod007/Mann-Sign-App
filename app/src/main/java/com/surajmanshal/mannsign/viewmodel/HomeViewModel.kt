package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsign.data.model.SubCategory
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.network.NetworkService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {


    private var _msg = MutableLiveData<String>()
    val msg : LiveData<String> get() = _msg

    private var _subCategories = MutableLiveData<List<SubCategory>>(mutableListOf())
    val subCategories : LiveData<List<SubCategory>> get() = _subCategories

    private var _products = MutableLiveData<List<Product>>(mutableListOf())
    val products : LiveData<List<Product>> get() = _products

    private var _isLoading = MutableLiveData<Boolean>(true)
    val isLoading : LiveData<Boolean> get() = _isLoading

    fun getSubCategories(){
        _isLoading.postValue(true)
        try{
            val r = NetworkService.networkInstance.fetchSubCategories()
            r.enqueue(object : Callback<List<SubCategory>?> {
                override fun onResponse(
                    call: Call<List<SubCategory>?>,
                    response: Response<List<SubCategory>?>
                ) {
                    _subCategories.postValue(response.body()!!)
                    _isLoading.postValue(false)
                }
                override fun onFailure(call: Call<List<SubCategory>?>, t: Throwable) {
                    _msg.postValue("Failure"+t.message.toString())
                    _isLoading.postValue(false)
                }
            })
        }catch (e : Exception){
            _msg.postValue(e.message.toString())
            _isLoading.postValue(false)
        }

    }

    fun getAllPosters(){
        _isLoading.postValue(true)
        val r = NetworkService.networkInstance.fetchAllPosters()
        r.enqueue(object : Callback<List<Product>?> {
            override fun onResponse(
                call: Call<List<Product>?>,
                response: Response<List<Product>?>
            ) {
                _products.postValue(response.body()!!)

            }

            override fun onFailure(call: Call<List<Product>?>, t: Throwable) {
                _isLoading.postValue(false)
                _msg.postValue(t.message.toString())
            }
        })

    }
}
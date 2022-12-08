package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.network.NetworkService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductCategoryDetailsViewModel : ViewModel() {

    private var _products = MutableLiveData<List<Product>>()
    val products : LiveData<List<Product>> get() = _products

    companion object{
        val db = NetworkService.networkInstance
    }

    fun loadProductByCat(id : Int){
        val r = db.fetchAllPosters()
        r.enqueue(object : Callback<List<Product>?> {
            override fun onResponse(
                call: Call<List<Product>?>,
                response: Response<List<Product>?>
            ) {
                val l = response.body()!!.filter {
                    it.subCategory == id
                }
                _products.postValue(l)
            }

            override fun onFailure(call: Call<List<Product>?>, t: Throwable) {

            }
        })
    }
}
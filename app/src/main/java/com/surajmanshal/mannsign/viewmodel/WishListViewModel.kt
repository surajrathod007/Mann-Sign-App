package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.MutableLiveData
import com.surajmanshal.mannsign.data.model.product.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WishListViewModel : ClientViewModel() {

    private val _wishListedProducts = MutableLiveData<MutableList<Product>>(mutableListOf())
    val wishListedProducts : MutableLiveData<MutableList<Product>> get() = _wishListedProducts

    private val _loading = MutableLiveData<Boolean>(false)
    val loading : MutableLiveData<Boolean> get() = _loading

    fun getMyWishList(productIds: List<Int>){
        _loading.postValue(true)
        repository
            .getProductsByIds(productIds)
            .enqueue(object : Callback<List<Product>?> {
                override fun onResponse(
                    call: Call<List<Product>?>,
                    response: Response<List<Product>?>
                ) {
                    _loading.postValue(false)
                    response.body()?.let {
                        _wishListedProducts.postValue(it as MutableList<Product>?)
                    }
                }
                override fun onFailure(call: Call<List<Product>?>, t: Throwable) {
                    _loading.postValue(false)
                    //TODO("Not yet implemented")
                }
            })
    }
}
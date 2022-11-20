package com.surajmanshal.mannsignadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsignadmin.data.model.*
import com.surajmanshal.mannsignadmin.data.model.product.ProductType
import com.surajmanshal.mannsignadmin.repository.Repository
import com.surajmanshal.response.SimpleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class PricingViewModel : ViewModel(), Serializable {
    private val repository = Repository()
    // -------------- LIVE DATA -------------------------------------------
    private val _productTypes = MutableLiveData<List<ProductType>>(listOf())
    val productTypes: LiveData<List<ProductType>> get() = _productTypes             // PRODUCTS
    private val _materials = MutableLiveData<List<Material>>(listOf())
    val material: LiveData<List<Material>> get() = _materials                          // MATERIALS
    private val _areas = MutableLiveData<List<Area>>(listOf())
    val areas: LiveData<List<Area>> get() = _areas                                      // AREAS
    private val _discounts = MutableLiveData<List<DiscountCoupon>>(listOf())
    val discounts : LiveData<List<DiscountCoupon>> get() = _discounts                      // DISCOUNTS
    private val _serverResponse = MutableLiveData<SimpleResponse>()
    val serverResponse : LiveData<SimpleResponse> get() = _serverResponse               //SERVER RESPONSE
    private val _allowAdding = MutableLiveData<Boolean>(false)
    val allowAdding : LiveData<Boolean> get() = _allowAdding

    fun getProductTypes(){
        val response = repository.fetchProductTypes()
        response.enqueue(object : Callback<List<ProductType>> {
            override fun onResponse(
                call: Call<List<ProductType>>,
                response: Response<List<ProductType>>
            ) {
                print(response.body().toString())
                response.body()?.let {
                    _productTypes.value = it
                }
            }
            override fun onFailure(call: Call<List<ProductType>>, t: Throwable) {
                //TODO("Not yet implemented")
            }
        })
    }

    fun getMaterials(){
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

    fun getAreas(){
        val response = repository.getAreas()
        println("Response is $response")
        response.enqueue(object : Callback<List<Area>> {
            override fun onResponse(call: Call<List<Area>>, response: Response<List<Area>>) {
                println("Inner Response is $response")
                response.body()?.let { _areas.value = it }
            }
            override fun onFailure(call: Call<List<Area>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }
    fun getCoupons(){
        val response = repository.getCoupons()
        println("Response is $response")
        response.enqueue(object : Callback<List<DiscountCoupon>> {

            override fun onResponse(
                call: Call<List<DiscountCoupon>>,
                response: Response<List<DiscountCoupon>>
            ) {
                println("Inner Response is $response")
                response.body()?.let { _discounts.value = it }
            }
            override fun onFailure(call: Call<List<DiscountCoupon>>, t: Throwable) {
                println("Failure is $t")
            }
        })
    }

    suspend fun setNewPrice(typeId: Any, newPrice: Float, changeFor: Int) {
        try {
            val response = repository.updatePrice(typeId,newPrice,changeFor)
            _serverResponse.postValue(response)
        }catch (e : Exception){
            println("$e")
        }
    }
    fun allowToAdd(isAllowed:Boolean = false){
        _allowAdding.value = isAllowed
    }

   suspend fun addCoupon(coupon: DiscountCoupon) {
       try {
           val response = repository.insertCoupon(coupon)
           _serverResponse.postValue(response)
       }catch (e : Exception){
           println("$e")
       }
   }
}

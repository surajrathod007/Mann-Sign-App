package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsign.data.model.ordering.CartItem
import com.surajmanshal.mannsign.data.model.ordering.Carts
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.network.NetworkService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.http2.Http2Stream
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartViewModel : ViewModel() {

    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

    val isLoading = MutableLiveData<Boolean>(true)
    private var _msg = MutableLiveData<String>()

    var _discount = MutableLiveData<Float>(0f)
    var _delivery = MutableLiveData<Float>(0f)
    //var _subTotal = MutableLiveData<Float>(0f)
    var _total = MutableLiveData<Float>(0f)
    var _amountToPay = MutableLiveData<Float>(0f)

    val msg: LiveData<String> get() = _msg

    companion object {
        val db = NetworkService.networkInstance
    }

    fun getCartItems(email: String) {

        isLoading.postValue(true)
        var r = db.fetchCartByEmail(email)

        r.enqueue(object : Callback<List<CartItem>?> {
            override fun onResponse(
                call: Call<List<CartItem>?>,
                response: Response<List<CartItem>?>
            ) {
                val list = response.body()!!
                _cartItems.postValue(list)
                _msg.postValue("Done ! ${list.size}")
                isLoading.postValue(false)
            }

            override fun onFailure(call: Call<List<CartItem>?>, t: Throwable) {
                isLoading.postValue(false)
                _msg.postValue(t.message.toString())
            }
        })
    }

    fun removeCart(id : Int){
        val r = db.removeCartItem(id)
        r.enqueue(object : Callback<SimpleResponse?> {
            override fun onResponse(
                call: Call<SimpleResponse?>,
                response: Response<SimpleResponse?>
            ) {
                val r = response.body()!!
                if(r.success){
                    _msg.postValue(r.message)
                }else{
                    _msg.postValue(r.message)
                }
            }

            override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                _msg.postValue(t.message)
            }
        })
    }

    //TODO : Get delivery and discount from mutable live data
    fun placeOrder(carts : Carts,discount : Float,delivery : Float ){
        val r = db.placeOrder(carts,discount,delivery)
        r.enqueue(object : Callback<SimpleResponse?> {
            override fun onResponse(
                call: Call<SimpleResponse?>,
                response: Response<SimpleResponse?>
            ) {
                _msg.postValue(response.body()!!.message)
            }

            override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                _msg.postValue(t.message.toString())
            }
        })
    }


    //TODO : Delivery calculation need to be done later
    fun doCalculation(){

            val lst = _cartItems.value
            var total = 0f
            if(!lst.isNullOrEmpty()){
                lst.forEach {
                    total += it.totalPrice
                }
                _total.postValue(total)
                _amountToPay.postValue(_total.value!!-_discount.value!!)
            }


    }

    fun useCoupon(code : String){
        val r = db.userCoupon(code)
        r.enqueue(object : Callback<Int?> {
            override fun onResponse(call: Call<Int?>, response: Response<Int?>) {

                when(response.code()){
                    200 -> {
                        _msg.postValue("Discount applied !")
                        var d = response.body()!!
                        val dis = (d*_total.value!!)/100
                        _discount.postValue(dis)
                        _amountToPay.postValue(_total.value!!-dis)
                    }
                    400 ->{
                        _msg.postValue("Invalid coupon code !" + response.message())
                    }
                }

            }

            override fun onFailure(call: Call<Int?>, t: Throwable) {
                _msg.postValue(t.message.toString())
            }
        })
    }
}
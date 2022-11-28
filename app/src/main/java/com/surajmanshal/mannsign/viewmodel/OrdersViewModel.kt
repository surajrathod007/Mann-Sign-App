package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsign.data.model.*
import com.surajmanshal.mannsign.data.model.auth.User
import com.surajmanshal.mannsign.data.model.ordering.Order
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.repository.Repository
import com.surajmanshal.mannsign.data.response.SimpleResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersViewModel : ViewModel() {

    //lateinit var  repository : Repository

    private val _allOrders = MutableLiveData<List<Order>>(emptyList())
    val allOrders: LiveData<List<Order>> get() = _allOrders

    val isEmptyList = MutableLiveData<Boolean>(false)
    val isLoading = MutableLiveData<Boolean>(true)
    private val _serverResponse = MutableLiveData<SimpleResponse>()
    val serverResponse : LiveData<SimpleResponse> get() = _serverResponse
    private val _user = MutableLiveData<User>()
    val user : LiveData<User> get() = _user

    //orderitem size,language and materials
    val _size = MutableLiveData<Size>()
    val size : LiveData<Size> get() = _size

    val _material = MutableLiveData<Material>()
    val material : LiveData<Material> get() = _material

    val _language = MutableLiveData<Language>()
    val langauge : LiveData<Language> get() = _language

    val _reviews = MutableLiveData<List<Review>>()
    val reviews : LiveData<List<Review>> get() = _reviews

    val _customerOrders = MutableLiveData<List<Order>>(mutableListOf())
    val customerOrders : LiveData<List<Order>> get() = _customerOrders

    private var _msg = MutableLiveData<String>()
    val msg : LiveData<String> get() = _msg


    companion object {
        val repository = Repository()
        val db = NetworkService.networkInstance
    }

    suspend fun setupViewModelDataMembers() {
        //CoroutineScope(Dispatchers.IO).launch { getAllOrders() }
    }

    fun getCustomerOrders(email : String){
        isLoading.postValue(true)
        val r = db.getOrderByEmail(email)
        r.enqueue(object : Callback<List<Order>?> {
            override fun onResponse(call: Call<List<Order>?>, response: Response<List<Order>?>) {
                if(response.body()!!.isEmpty()){
                    _msg.postValue("No Orders , Please Order Something!")
                }
                _customerOrders.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<List<Order>?>, t: Throwable) {
                _msg.postValue(t.message.toString())
            }
        })
    }

    fun filterOrder(status: Int) {
        val list = _allOrders.value?.filter { it.orderStatus == status }
        _allOrders.value = list!!
    }

    fun refreshOrders(){
        _allOrders.postValue(_allOrders.value)
    }

    fun getAllOrders() {
        isLoading.postValue(true)
        val v = repository.fetchAllOrders()
        v.enqueue(object : Callback<List<Order>?> {
            override fun onResponse(call: Call<List<Order>?>, response: Response<List<Order>?>) {
                response.body().let {
                    _allOrders.value = it
                    if (it.isNullOrEmpty()) {
                        isEmptyList.postValue(true)
                    }
                }
                isLoading.postValue(false)
            }

            override fun onFailure(call: Call<List<Order>?>, t: Throwable) {

                isLoading.postValue(false)
            }
        })
    }

    suspend fun updateOrder(order: Order) {

            try {
                val response = NetworkService.networkInstance.updateOrder(order)
                response.enqueue(object : Callback<SimpleResponse?> {
                    override fun onResponse(
                        call: Call<SimpleResponse?>,
                        response: Response<SimpleResponse?>
                    ) {
                        _serverResponse.postValue(response.body())

                        //this is not working
                        /*
                        CoroutineScope(Dispatchers.IO).launch {
                            getAllOrders()
                            refreshOrders()
                        }
                         */

                    }

                    override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                        _serverResponse.postValue(SimpleResponse(true, message = "Some error"))
                    }
                })
            }catch (e : Exception){
                _serverResponse.postValue(SimpleResponse(true, message = e.message.toString()))
            }



    }

    suspend fun fetchUserByEmail(email : String){

        try {

            val u = NetworkService.networkInstance.fetchUserByEmail(email)
            u.enqueue(object : Callback<User?> {
                override fun onResponse(call: Call<User?>, response: Response<User?>) {
                    _user.postValue(response.body()!!)
                }

                override fun onFailure(call: Call<User?>, t: Throwable) {
//                    TODO("Not yet implemented")
                }
            })

        }catch (e : Exception){
            _serverResponse.postValue(SimpleResponse(true,"${e.message.toString()}"))
        }
    }

    fun fetchOrderItemDetails(sid : Int,lid :  Int,mid : Int){
        try{
            val size = NetworkService.networkInstance.fetchSizeById(sid)

            size.enqueue(object : Callback<Size?> {
                override fun onResponse(call: Call<Size?>, response: Response<Size?>) {
                    _size.postValue(response.body()!!)
                }

                override fun onFailure(call: Call<Size?>, t: Throwable) {
                    _serverResponse.postValue(SimpleResponse(true,"Size fetch failure"))
                }
            })

            val lan = NetworkService.networkInstance.fetchLanguageById(lid)
            lan.enqueue(object : Callback<Language?> {
                override fun onResponse(call: Call<Language?>, response: Response<Language?>) {
                    _language.postValue(response.body()!!)
                }

                override fun onFailure(call: Call<Language?>, t: Throwable) {
                    _serverResponse.postValue(SimpleResponse(true,"Language fetch failure"))
                }
            })

            val mat = NetworkService.networkInstance.fetchMaterialById(mid)
            mat.enqueue(object : Callback<Material?> {
                override fun onResponse(call: Call<Material?>, response: Response<Material?>) {
                    _material.postValue(response.body()!!)
                }

                override fun onFailure(call: Call<Material?>, t: Throwable) {
                    _serverResponse.postValue(SimpleResponse(true,"Material fetch failure"))
                }
            })

        }catch ( e : Exception){
            _serverResponse.postValue(SimpleResponse(true,"${e.message}"))
        }
    }

    fun fetchProductReview(productId : String){
        val l = NetworkService.networkInstance.getReview(productId)
        l.enqueue(object : Callback<List<Review>?> {
            override fun onResponse(call: Call<List<Review>?>, response: Response<List<Review>?>) {
                _reviews.postValue(response.body())
            }

            override fun onFailure(call: Call<List<Review>?>, t: Throwable) {

            }
        })
    }

}
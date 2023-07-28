package com.surajmanshal.mannsign.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsign.data.model.SubCategory
import com.surajmanshal.mannsign.data.model.auth.LoginReq
import com.surajmanshal.mannsign.data.model.product.MainPoster
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.room.LocalDatabase
import com.surajmanshal.mannsign.room.wishlist.WishListDao
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    var wishListDao : WishListDao? = null
    var catname = "Default category"
    private var _msg = MutableLiveData<String>()
    val msg: LiveData<String> get() = _msg

    private var _subCategories = MutableLiveData<List<SubCategory>>(mutableListOf())
    val subCategories: LiveData<List<SubCategory>> get() = _subCategories

    private var _products = MutableLiveData<List<Product>>(mutableListOf())
    val products: LiveData<List<Product>> get() = _products

    private var _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _posters = MutableLiveData<List<MainPoster>>()
    val posters: LiveData<List<MainPoster>> get() = _posters

    private var _subName = MutableLiveData<String>()
    val subName : LiveData<String> get() = _subName

    private var _isLoggedOut = MutableLiveData<Boolean>(false)
    val isLoggedOut : LiveData<Boolean> get() = _isLoggedOut

    companion object {
        val db = NetworkService.networkInstance
    }

    fun getSubCategories() {
        _isLoading.postValue(true)
        try {
            val r = db.fetchSubCategories()
            r.enqueue(object : Callback<List<SubCategory>?> {
                override fun onResponse(
                    call: Call<List<SubCategory>?>,
                    response: Response<List<SubCategory>?>
                ) {
                    if(response.body() != null){
                        _subCategories.postValue(response.body()!!)
                    }
                    _isLoading.postValue(false)
                }

                override fun onFailure(call: Call<List<SubCategory>?>, t: Throwable) {
                    _msg.postValue("Failure" + t.message.toString())
                    _isLoading.postValue(false)
                }
            })
        } catch (e: Exception) {
            _msg.postValue(e.message.toString())
            _isLoading.postValue(false)
        }
    }
    fun getAllPosters() {
        _isLoading.postValue(true)
        val r = db.fetchPagedPosters(1)
        r.enqueue(object : Callback<List<Product>?> {
            override fun onResponse(
                call: Call<List<Product>?>,
                response: Response<List<Product>?>
            ) {
                //_products.postValue(response.body()!!)
                response.body()?.let { data->
                    val mylist = mutableListOf<MainPoster>()
                    val subCats = data.map {
                        it.subCategory
                    }.distinct().toMutableList()
                    subCats.reverse()
                    subCats.forEach {
                        val l = mutableListOf<Product>().apply{
                            addAll(data.filter { p ->
                                p.subCategory == it
                            })
                            add(Product(-1).apply { subCategory = it })
                        }
                        mylist.add(MainPoster(subCategory = it.toString(), posters = l))
                    }
                    _posters.postValue(mylist)
                }
                _isLoading.postValue(false)
            }

            override fun onFailure(call: Call<List<Product>?>, t: Throwable) {
                _isLoading.postValue(false)
                _msg.postValue(t.message.toString())
            }
        })

    }

    fun getSubCategoryById(id: Int,onResponse : (String) -> Unit) : String{
        val response = db.fetchSubCategoryById(id)
        response.enqueue(object : Callback<SubCategory> {
            override fun onResponse(call: Call<SubCategory>, response: Response<SubCategory>) {
                println(response.body())
                response.body()?.let {
                    _subName.postValue(it.name)
                    onResponse(it.name)
                }
            }
            override fun onFailure(call: Call<SubCategory>, t: Throwable) {

            }
        })
        return catname
    }

    fun setDeviceID(log : LoginReq){
        val r = db.setDeviceId(log)
        r.enqueue(object : Callback<SimpleResponse?> {
            override fun onResponse(
                call: Call<SimpleResponse?>,
                response: Response<SimpleResponse?>
            ) {

            }

            override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                _msg.postValue(t.message.toString())
            }
        })
    }

    fun logout(email : String,token : String){
        try{
            val r = db.logout(email,token)
            r.enqueue(object : Callback<SimpleResponse?> {
                override fun onResponse(
                    call: Call<SimpleResponse?>,
                    response: Response<SimpleResponse?>
                ) {
                    val r = response.body()!!
                    if(r.success){
                        _isLoggedOut.postValue(true)
                    }
                }
                override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                    _msg.postValue(t.message.toString())
                }
            })
        }catch (e : Exception){
            _msg.postValue(e.message.toString())
        }

    }

    fun setUpWishlistSource(context : Context): WishListDao {
        LocalDatabase.getDatabase(context).wishListDao().apply {
            wishListDao = this
            return this
        }
    }
}
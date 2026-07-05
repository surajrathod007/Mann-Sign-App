package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.network.NetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductCategoryDetailsViewModel : ViewModel() {

    private var _products = MutableLiveData<List<Product>>()
    val products : LiveData<List<Product>> get() = _products

    private var _filteredProducts =  MutableLiveData<List<Product>>()
    val filteredProducts : LiveData<List<Product>> get() = _filteredProducts

    private var _msg = MutableLiveData<String>()
    val msg: LiveData<String> get() = _msg

    private var _isLoading = MutableLiveData<Boolean>(true)
    val isLoading : LiveData<Boolean> get() = _isLoading

    companion object{
        val db = NetworkService.networkInstance
    }

    fun loadProductByCat(id: Int, name: String? = null, done: (Boolean) -> Unit){
        _isLoading.postValue(true)
        val r = db.fetchAllPosters()
        r.enqueue(object : Callback<List<Product>?> {
            override fun onResponse(
                call: Call<List<Product>?>,
                response: Response<List<Product>?>
            ) {
                if(!name.isNullOrEmpty()){

                    val l = response.body()!!.filter {
                        it.posterDetails!!.title.contains(name,true)
                    }
                    _products.postValue(response.body()!!)
                    _filteredProducts.postValue(l)

                    _isLoading.postValue(false)
                }else{

                    val l = response.body()!!.filter {
                        it.subCategory == id
                    }
                    _products.postValue(response.body()!!)
                    _filteredProducts.postValue(l)
                    done(true)
                    _isLoading.postValue(false)
                }
            }

            override fun onFailure(call: Call<List<Product>?>, t: Throwable) {
                _isLoading.postValue(false)
            }
        })
    }

    private var searchJob: Job? = null

    fun searchProduct(name : String){
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.Default) {
            delay(500)
            val allProducts = _products.value ?: return@launch
            val l = allProducts.filter {
                it.posterDetails!!.title.contains(name,true)
            }
            _filteredProducts.postValue(l)
        }
    }
}
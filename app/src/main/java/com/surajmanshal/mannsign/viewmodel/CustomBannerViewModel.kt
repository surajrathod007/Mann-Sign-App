package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsign.data.model.Material
import com.surajmanshal.mannsign.data.model.product.Banner
import com.surajmanshal.mannsign.data.model.product.Poster
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.network.NetworkService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomBannerViewModel : ViewModel() {

    private var _msg = MutableLiveData<String>()
    val msg: LiveData<String> get() = _msg

    private var _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _allMaterials = MutableLiveData<List<Material>>()
    val allMaterials: LiveData<List<Material>> get() = _allMaterials

    var _currentProduct = MutableLiveData<Product>()

    companion object {
        val db = NetworkService.networkInstance
    }

    fun setProductType(type: Int, poster: Poster? = null, banner: Banner? = null) {
        if (type == 0) {
            //set poster
            _currentProduct.value?.bannerDetails = null
            _currentProduct.value.apply {
                this?.posterDetails = poster
            }
            _msg.postValue("Poster applied")
        }
        if (type == 1) {
            //set banner
            _currentProduct.value?.posterDetails = null
            _currentProduct.value.apply {
                this?.bannerDetails = banner
            }
            _msg.postValue("Banner applied")
        }
    }

    fun getAllMaterials() {
        val r = db.fetchMaterials()
        r.enqueue(object : Callback<List<Material>?> {
            override fun onResponse(
                call: Call<List<Material>?>,
                response: Response<List<Material>?>
            ) {
                if (!response.body().isNullOrEmpty()) {
                    _allMaterials.postValue(response.body()?.sortedBy {
                        it.id
                    })
                }
            }

            override fun onFailure(call: Call<List<Material>?>, t: Throwable) {
                _msg.postValue("Failure in fetching materials ${t.message}")
            }
        })
    }

}
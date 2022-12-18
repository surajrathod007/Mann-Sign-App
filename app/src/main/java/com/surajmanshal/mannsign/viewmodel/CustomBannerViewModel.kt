package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsign.data.model.product.Banner
import com.surajmanshal.mannsign.data.model.product.Poster
import com.surajmanshal.mannsign.data.model.product.Product

class CustomBannerViewModel : ViewModel() {

    private var _msg = MutableLiveData<String>()
    val msg: LiveData<String> get() = _msg

    private var _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    var _currentProduct = MutableLiveData<Product>()

    fun setProductType(type : Int,poster : Poster?=null,banner : Banner? = null){
        if(type==0){
            //set poster
            _currentProduct.value?.bannerDetails = null
            _currentProduct.value.apply {
                this?.posterDetails = poster
            }
            _msg.postValue("Poster applied")
        }
        if(type==1){
            //set banner
            _currentProduct.value?.posterDetails = null
            _currentProduct.value.apply {
                this?.bannerDetails = banner
            }
            _msg.postValue("Banner applied")
        }
    }

}
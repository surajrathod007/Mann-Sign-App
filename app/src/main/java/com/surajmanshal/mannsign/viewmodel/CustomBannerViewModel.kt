package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.surajmanshal.mannsign.data.model.Material
import com.surajmanshal.mannsign.data.model.Variant
import com.surajmanshal.mannsign.data.model.product.Banner
import com.surajmanshal.mannsign.data.model.product.Poster
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.utils.Constants
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomBannerViewModel : ClientViewModel() {

    private var _msg = MutableLiveData<String>()
    val msg: LiveData<String> get() = _msg

    private var _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _allMaterials = MutableLiveData<List<Material>>()
    val allMaterials: LiveData<List<Material>> get() = _allMaterials


    var orderPlaced = MutableLiveData<Boolean>(false)
    private val _imageUploadResponse = MutableLiveData<SimpleResponse>()
    val imageUploadResponse : LiveData<SimpleResponse> get() = _imageUploadResponse     // IMAGE UPLOADING PROGRESS
    private val _productUploadResponse = MutableLiveData<Variant>()
    val productUploadResponse : LiveData<Variant> get() = _productUploadResponse  // PRODUCT UPLOADING PROGRESS

    var _currentProduct = MutableLiveData<Product>()
    val _currentMaterial = MutableLiveData<Material>()
    val _currentProductTypeId = MutableLiveData(Constants.TYPE_POSTER)

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

    fun getAllMaterials(productTypeIds : List<Int>) {
        val r = repository.fetchMaterials(productTypeIds)
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

    suspend fun sendImage(part: MultipartBody.Part, languageId: Int){
        try {
            val response = repository.uploadProductImage(part,languageId)
            _serverResponse.postValue(response)
            _imageUploadResponse.postValue(response)
        }catch (e : Exception){
            println("$e ${serverResponse.value?.message}")
        }
    }

    suspend fun addProduct(product: Product) {
        try {
            val response = repository.sendProduct(product)
            _productUploadResponse.postValue(response)
        }catch (e : Exception){
            println("$e")
        }
    }

    fun setMaterialId(index: Int) {
        _currentMaterial.postValue(
            _allMaterials.value?.get(index)
        )
    }

    fun addCustomOrder(variant: Variant,delivery : Float,email : String){
        try {
            val r = db.addCustomOrder(variant,delivery,email)
            r.enqueue(object : Callback<SimpleResponse?> {
                override fun onResponse(
                    call: Call<SimpleResponse?>,
                    response: Response<SimpleResponse?>
                ) {
                    _msg.postValue("${response.body()?.message}")
                    orderPlaced.value = true
                }

                override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                    _msg.postValue("Failure : ${t.message}")
                }
            })
        }catch (e : Exception){
            _msg.postValue("Exception : $e")
        }
    }

}
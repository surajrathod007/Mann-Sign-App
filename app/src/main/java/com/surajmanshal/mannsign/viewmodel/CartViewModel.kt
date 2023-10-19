package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsign.data.model.Area
import com.surajmanshal.mannsign.data.model.Language
import com.surajmanshal.mannsign.data.model.Material
import com.surajmanshal.mannsign.data.model.Size
import com.surajmanshal.mannsign.data.model.Variant
import com.surajmanshal.mannsign.data.model.auth.User
import com.surajmanshal.mannsign.data.model.ordering.CartItem
import com.surajmanshal.mannsign.data.model.ordering.Carts
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.repository.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartViewModel : ViewModel() {

    val repository = Repository()
    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

    val isLoading = MutableLiveData<Boolean>(true)
    private var _msg = MutableLiveData<String>()
    val msg: LiveData<String> get() = _msg

    var _discount = MutableLiveData<Float>(0f)
    var _delivery = MutableLiveData<Float>(0f)
    //var _subTotal = MutableLiveData<Float>(0f)
    var _total = MutableLiveData<Float>(0f)
    var _amountToPay = MutableLiveData<Float>(0f)

    var _areas = MutableLiveData<List<Area>>()

    var _currrentProductInCartVariants = MutableLiveData<List<Variant>>(emptyList())

    var _orderPlaced = MutableLiveData<SimpleResponse>()
    val orderPlaced : LiveData<SimpleResponse> get() = _orderPlaced

    var _showScroll = MutableLiveData<Boolean>(false)
    val showScroll : LiveData<Boolean> get() = _showScroll
    val _selectedVariant = MutableLiveData<Variant>(Variant())
    val _selectedSize = MutableLiveData<Size>()
    val _selectedMaterial = MutableLiveData<Material>()
    val _selectedLanguage = MutableLiveData<Language>()

    private val _serverResponse = MutableLiveData<SimpleResponse>()
    val serverResponse : LiveData<SimpleResponse> get() = _serverResponse               //SERVER RESPONSE

    init {
        getArea()
    }
    companion object {
        val db = NetworkService.networkInstance
    }

    fun setScrollVisibility(v : Boolean){
        _showScroll.postValue(v)
    }

    fun getCartItems(email: String,loading : Boolean = true) {

        isLoading.postValue(loading)
        var r = db.fetchCartByEmail(email)

        r.enqueue(object : Callback<List<CartItem>?> {
            override fun onResponse(
                call: Call<List<CartItem>?>,
                response: Response<List<CartItem>?>
            ) {
                val list = response.body()!!.sortedBy {
                    it.cartItemId
                }
                _cartItems.postValue(list)
                /*if(list.isEmpty()){
                    _msg.postValue("No Cart Items !")
                }*/
                isLoading.postValue(false)
            }

            override fun onFailure(call: Call<List<CartItem>?>, t: Throwable) {
                isLoading.postValue(false)
                _msg.postValue(t.message.toString())
            }
        })


    }

    fun removeCart(id : Int,email : String = ""){
        val r = db.removeCartItem(id)
        r.enqueue(object : Callback<SimpleResponse?> {
            override fun onResponse(
                call: Call<SimpleResponse?>,
                response: Response<SimpleResponse?>
            ) {
                val r = response.body()!!
                if(r.success){
                    _msg.postValue(r.message)
                    getCartItems(email)
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
    fun placeOrder(){
        isLoading.postValue(true)
        val cart = mutableListOf<CartItem>()
        _cartItems.value!!.forEach {
            cart.add(it)
        }
        val carts = Carts(cart)

        val r = db.placeOrder(carts,_discount.value!!,_delivery.value!!)
        r.enqueue(object : Callback<SimpleResponse?> {
            override fun onResponse(
                call: Call<SimpleResponse?>,
                response: Response<SimpleResponse?>
            ) {


                response.body()?.let {
                    _msg.postValue(it.message)
                    _orderPlaced.postValue(it)
                }

                isLoading.postValue(false)
            }

            override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                _msg.postValue(t.message.toString())
                isLoading.postValue(false)
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
        isLoading.postValue(true)
        val r = db.userCoupon(code)
        r.enqueue(object : Callback<Int?> {
            override fun onResponse(call: Call<Int?>, response: Response<Int?>) {

                when(response.code()){
                    200 -> {
                        _msg.postValue("Discount applied !")
                        var d = response.body()!!
                        val dis = (d*_total.value!!)/100
                        _discount.postValue(dis)
                        _amountToPay.postValue((_total.value!!-dis)+_delivery.value!!)
                    }
                    400 ->{
                        _msg.postValue("Invalid coupon code !" + response.message())
                    }
                }
                isLoading.postValue(false)
                _showScroll.postValue(true)
            }

            override fun onFailure(call: Call<Int?>, t: Throwable) {
                _msg.postValue(t.message.toString())
                isLoading.postValue(false)
            }
        })
    }

    fun getUser(email : String,user : (User?)-> Unit){
        val r = db.fetchUserByEmail(email)
        r.enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                if(response.body()!=null){
                    user(response.body()!!)
                }else{
                    user(null)
                    _msg.postValue("Error while fetching user")
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                _msg.postValue("Failure while fetching user ${t.message}")
                user(null)
            }
        })
    }

    fun getArea(){
        val r = db.fetchAreas()
        r.enqueue(object : Callback<List<Area>?> {
            override fun onResponse(call: Call<List<Area>?>, response: Response<List<Area>?>) {
                if(response.body()!=null){
                    _areas.postValue(response.body()!!)

                    _delivery.postValue(response.body()!!.random().minCharge)       //TODO : Get for the user address
                }
            }

            override fun onFailure(call: Call<List<Area>?>, t: Throwable) {
                _msg.postValue("Error in fetching areas")
            }
        })
    }

    fun clearValues(){
        _total.postValue(0f)
        _amountToPay.postValue(0f)
        //_delivery.postValue(0f)
        _discount.postValue(0f)
        _orderPlaced.postValue(SimpleResponse(false,"none"))
    }


    fun setVariantSize(size: Size?) {
        _selectedSize.postValue(size)
    }
    fun setVariantMaterial(material: Int?) {
        _selectedVariant.value?.materialId = material
        refreshVariant()
    }
    fun setVariantLanguage(languageId: Int?) {
        _selectedVariant.value?.languageId = languageId
        refreshVariant()
    }
    fun setVariantPrice(price : Float){
        _selectedVariant.value?.variantPrice = price
        refreshVariant()
    }

    fun refreshVariant() = _selectedVariant.postValue(_selectedVariant.value)

    suspend fun addToCart(email: String, variant: Variant, qty : Int = 1 ){
        try {
            val response = repository.addToCart(email,variant,qty)
            _serverResponse.postValue(response)
        }catch (e : Exception){
            println("Failed to add into cart : $e")
        }
    }

    fun getMyCartVariants(email: String,productId : Int){
        try {
            val response = repository.fetchProductVariants(email,productId)
            println("response is $response")
            response.enqueue(object : Callback<List<Variant>?> {
                override fun onResponse(
                    call: Call<List<Variant>?>,
                    response: Response<List<Variant>?>
                ) {
                    println("inner response is $response")
                    response.body()?.let{
                        _currrentProductInCartVariants.postValue(it)
                    }
                }
                override fun onFailure(call: Call<List<Variant>?>, t: Throwable) {
                    println("Failed to fetch cartItem variants : $t")
                }
            })
        }catch (e : Exception){
            println("$e")
        }
    }

    /*suspend fun isProductInCart(){
        try {
            repository.
        }
    }*/

    fun updateCart(cartId : Int,qty: Int,email : String = ""){
        //isLoading.postValue(true)
        val r = db.updateCart(cartId,qty)
        r.enqueue(object : Callback<SimpleResponse?> {
            override fun onResponse(
                call: Call<SimpleResponse?>,
                response: Response<SimpleResponse?>
            ) {
                val r = response.body()
                if(r != null){
                    //_msg.postValue(r.message)
                    getCartItems(email,false)
                }else{
                    _msg.postValue("Response is null")
                }
                isLoading.postValue(false)
            }

            override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                _msg.postValue(t.message.toString())
                isLoading.postValue(false)
            }
        })
    }
}
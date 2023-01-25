package com.surajmanshal.mannsign.network

import com.surajmanshal.mannsign.data.model.*
import com.surajmanshal.mannsign.data.model.auth.LoginReq
import com.surajmanshal.mannsign.data.model.auth.LoginResponse
import com.surajmanshal.mannsign.data.model.auth.User
import com.surajmanshal.mannsign.data.model.ordering.*
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.data.model.product.ProductType
import com.surajmanshal.mannsign.data.response.SimpleResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface NetworkCallsInterface {

    //User Auth
    @Headers("Content-Type: application/json")
    @POST("user/register")
    suspend fun registerUser(@Body user : User) : SimpleResponse

    @Headers("Content-Type: application/json")
    @POST("user/login")
    suspend fun loginUser(@Body loginReq: LoginReq) : LoginResponse

    @Headers("Content-Type: application/json")
    @POST("user/otp")
    suspend fun sendOtp(@Query("email") email : String) : SimpleResponse

    @Headers("Content-Type: application/json")
    @POST("user/resetpassword")
    suspend fun resetPassword(@Query("email") email: String, @Query("newpas") newpas : String) : SimpleResponse

    //user Update

    @Headers("Content-Type: application/json")
    @POST("user/update")
    suspend fun updateUser(@Body user : User) : SimpleResponse

    @GET("materials")
    fun fetchMaterials() : Call<List<Material>>

    @GET("languages")
    fun fetchLanguages() : Call<List<Language>>

    @GET("subcategories")
    fun fetchSubCategories() : Call<List<SubCategory>>

    @GET("categories")
    fun fetchCategories() : Call<List<Category>>

    @GET("sizes")
    fun fetchSystemSizes() : Call<List<Size>>

    @Headers("Content-Type: application/json")
    @POST("product/insert")
    suspend fun sendProduct(@Body product: Product) : SimpleResponse

    @GET("product/posters")
    fun fetchAllPosters() : Call<List<Product>>

    @GET("order/getall")
    fun fetchAllOrders() : Call<List<Order>>

    @Headers("Content-Type: application/json")
    @POST("order/update")
    fun updateOrder(@Body order: Order) : Call<SimpleResponse>

    @Multipart
    @POST("productImage/upload")
    suspend fun uploadProductImage(@Part image : MultipartBody.Part, @Query("languageId") languageId : Int) :SimpleResponse

    @Multipart
    @POST("profileImage/upload")
    suspend fun uploadProfileImage(@Part image : MultipartBody.Part) :SimpleResponse

    @GET("size")
    fun fetchSizeById(@Query("id") id:Int) : Call<Size>

    @GET("material")
    fun fetchMaterialById(@Query("id") id:Int) : Call<Material>

    @GET("language")
    fun fetchLanguageById(@Query("id") id:Int) : Call<Language>

    @POST("category/delete")
    suspend fun deleteCategory(@Query("id") id:Int) : SimpleResponse

    @POST("category/insert")
    suspend fun insertCategory(@Body category: Category): SimpleResponse

    @POST("subCategory/remove")
    suspend fun deleteSubCategory(@Query("id") id: Int): SimpleResponse

    @POST("subCategory/add")
    suspend fun insertSubCategory(@Body category: SubCategory): SimpleResponse


    @GET("category/subcategories")
    fun fetchSubCategoriesOfCategory(@Query("id") id : Int) : Call<List<SubCategory>>

    @GET("user/get")
    fun fetchUserByEmail(@Query("email") email : String) : Call<User>

    @GET("productTypes")
    fun fetchProductTypes(): Call<List<ProductType>>

    @POST("pricing/update")
    suspend fun updatePrice(@Query("id") typeId: Any, @Query("price") newPrice: Float, @Query("changeFor") changeFor: Int): SimpleResponse

    @GET("area/getAll")
    fun fetchAreas(): Call<List<Area>>

    @GET("transaction/getall")   //not added in repo
    fun fetchAllTransactions() : Call<List<Transaction>>

    @POST("transaction/filter")     //not added in repo
    fun filterTransaction(@Body dateFilter : DateFilter) : Call<List<Transaction>>

    @POST("order/filter")         //not addded in repo
    fun filterOrder(@Body dateFilter: DateFilter) : Call<List<Order>>

    @GET("discountCoupons")
    fun fetchCoupons(): Call<List<DiscountCoupon>>

    @POST("discount/add")
    suspend fun insertCoupon(@Query("code")couponCode: String,@Query("value") value: Int,@Query("qty") qty: Int): SimpleResponse

    @GET("user/getall")
    fun fetchAllUsers() : Call<List<User>>

    @POST("review/add")
    fun addReview(@Body review : Review) : Call<SimpleResponse>

    @POST("review/get")
    fun getReview(@Query("productId") productId : String) : Call<List<Review>>

    @POST("review/getUserReview")
    fun getUserReview(@Query("productId") productId : String, @Query("emailId") emailId : String) : Call<Review?>

    @POST("review/delete")
    fun deleteReview(@Query("reviewId") reviewId : String) : Call<SimpleResponse>

    @GET("category")
    fun fetchCategoryById(@Query("id") id: Int): Call<Category>

    @GET("subCategory")
    fun fetchSubCategoryById(@Query("id")id: Int): Call<SubCategory>

    @POST("cart/get")
    fun fetchCartByEmail(@Query("email") email : String) : Call<List<CartItem>>

    @POST("cart/remove")
    fun removeCartItem(@Query("cartid") cartid : Int) : Call<SimpleResponse>

    @POST("order/add")
    fun placeOrder(@Body carts : Carts, @Query("discount") discount : Float, @Query("delivery") delivery : Float) : Call<SimpleResponse>

    @GET("discount/use")
    fun userCoupon(@Query("code") code : String) : Call<Int>

    @POST("user/updateProfilePic")
    suspend fun updateUserProfilePic(@Query("emailId")emailId: String, @Query("imgUrl") imgUrl: String): SimpleResponse

    @POST("order/get")
    fun getOrderByEmail(@Query("email") email : String) : Call<List<Order>>

    @POST("order/id")
    fun getOrderById(@Query("id") id : String) : Call<Order>

    @POST("review/getAllUserReview")
    fun getAllUserReview(@Query("emailId") emailId: String) : Call<List<Review>>

    @POST("review/delete")
    fun deleteReview(@Query("reviewId") reviewId : Int) : Call<SimpleResponse>

    @POST("transaction/getUserTransaction")
    fun getUserTransactions(@Body date : DateFilter,@Query("emailId") emailId : String) : Call<List<Transaction>>

    @POST("transaction/getUserAllTransaction")
    fun getUserAllTransaction(@Query("emailId") emailId : String) : Call<List<Transaction>>

    @POST("user/updateDeviceId")
    fun setDeviceId(@Body loginReq: LoginReq) : Call<SimpleResponse>

    @POST("user/logout")
    fun logout(@Query("email") email : String,@Query("token") token : String) : Call<SimpleResponse>

    @POST("cart/add")
    suspend fun insertToCartItem(@Query("email") email: String,@Body variant: Variant,@Query("qty") qty: Int) : SimpleResponse

    @POST("cart/update")
    fun updateCart(@Query("cartid") cartid: Int,@Query("qty") qty: Int) : Call<SimpleResponse>

    @GET("cart/varientByEmail")
    fun fetchProductVariants(@Query("email") email: String,@Query("productId") productId: Int) : Call<List<Variant>>

    @GET("chat/getByOrderId")
    fun loadChats(@Query("orderId") orderId : String) : Call<List<ChatMessage>>

    @POST("chat/add")
    fun addChat(@Body msg : ChatMessage) : Call<SimpleResponse>

    @GET("user/getotp")
    fun sendOtpNew(@Query("email") email : String) : Call<SimpleResponse>

    @GET("order/canReview")
    fun canReview(@Query("emailId") email: String,@Query("productId") productId: Int) : Call<SimpleResponse>

    @Multipart
    @POST("chat/uploadImage")
    suspend fun uploadChatImage(@Part image: MultipartBody.Part) : SimpleResponse

    @POST("chat/add")
    suspend fun addImageChat(@Body msg : ChatMessage) : SimpleResponse

}

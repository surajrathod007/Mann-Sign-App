package com.surajmanshal.mannsign.network

import com.surajmanshal.mannsign.data.model.*
import com.surajmanshal.mannsign.data.model.auth.User
import com.surajmanshal.mannsign.data.model.ordering.Order
import com.surajmanshal.mannsign.data.model.ordering.Transaction
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.data.model.product.ProductType
import com.surajmanshal.mannsign.data.response.SimpleResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface NetworkCallsInterface {

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
    @POST("image/upload")
    suspend fun uploadImage(@Part image : MultipartBody.Part) :SimpleResponse

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


}
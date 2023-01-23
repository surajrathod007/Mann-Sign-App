package com.surajmanshal.mannsign.repository

import android.content.Context
import com.surajmanshal.mannsign.data.model.*
import com.surajmanshal.mannsign.data.model.ordering.Order
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.room.UserDatabase
import okhttp3.MultipartBody

open class Repository() {

    private val server = NetworkService.networkInstance // Remote
    private lateinit var room : UserDatabase // Local

    fun setupDataSources(context: Context){
        room = UserDatabase.getDatabase(context)
    }

   /* fun getUserByEmailId(email : String,activity: Activity) : User {
         room.userDao().getUser(email).observe(activity as LifecycleOwner){
             if(it==null){
                 return@observe it
             }else{

             }
         }
    }*/

    fun fetchMaterials() = server.fetchMaterials()

    fun fetchLanguages() = server.fetchLanguages()

    fun fetchSubCategories() = server.fetchSubCategories()

    fun fetchCategory() = server.fetchCategories()

    fun fetchSubcategories(id: Int) = server.fetchSubCategoriesOfCategory(id)

    fun fetchSizes() = server.fetchSystemSizes()

    fun fetchAllOrders() = server.fetchAllOrders()

    fun getAreas() = server.fetchAreas()

    suspend fun updateOrder(order: Order) = server.updateOrder(order)

    suspend fun sendProduct(product: Product) = server.sendProduct(product)

    fun fetchPosters() = server.fetchAllPosters()

    suspend fun uploadProductImage(part: MultipartBody.Part, languageId: Int) = server.uploadProductImage(part,languageId)

    suspend fun uploadProfileImage(part: MultipartBody.Part) = server.uploadProfileImage(part)

    suspend fun deleteCategory(id: Int) = server.deleteCategory(id)

    suspend fun insertCategory(category: Category) = server.insertCategory(category)

    suspend fun deleteSubCategory(id: Int) = server.deleteSubCategory(id)

    suspend fun fetchUserByEmail(email: String) = server.fetchUserByEmail(email)

    fun fetchProductTypes() = server.fetchProductTypes()

    suspend fun updatePrice(typeId: Any, newPrice: Float, changeFor: Int) = server.updatePrice(typeId, newPrice, changeFor)

    fun getCoupons() = server.fetchCoupons()

    suspend fun insertCoupon(coupon: DiscountCoupon) = server.insertCoupon(coupon.couponCode,coupon.value,coupon.qty)

    suspend fun insertSubCategory(category: SubCategory) = server.insertSubCategory(category)

    fun getCategoryById(id: Int) = server.fetchCategoryById(id)

    fun getSubCategoryById(id: Int) = server.fetchSubCategoryById(id)

    fun getMaterialById(id:Int) =  server.fetchMaterialById(id)

    fun getLanguageById(id:Int) =  server.fetchLanguageById(id)

    fun getReview(productId: String) = server.getReview(productId)

    fun canReview(emailId : String, productId: Int) = server.canReview(emailId,productId)

    fun addReview(review : Review) = server.addReview(review)

    suspend fun addToCart(email: String, variant: Variant, qty : Int) = server.insertToCartItem(email,variant,qty)

    fun fetchProductVariants(email: String, productId: Int) = server.fetchProductVariants(email,productId)

}
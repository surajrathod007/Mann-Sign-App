package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsign.data.model.Category
import com.surajmanshal.mannsign.data.model.SubCategory
import com.surajmanshal.mannsign.repository.Repository
import com.surajmanshal.mannsign.utils.LiveDataCode
import com.surajmanshal.mannsign.data.response.SimpleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryViewModel:ViewModel() {


    private val repository = Repository()
    // -------------- LIVE DATA -------------------------------------------
    private val _categories = MutableLiveData<MutableList<Category>>(mutableListOf())
    val categories: LiveData<MutableList<Category>> get() = _categories
    private val _subCategories = MutableLiveData<MutableList<SubCategory>>(mutableListOf())
    val subCategories: LiveData<MutableList<SubCategory>> get() = _subCategories
    private val _isDeleting = MutableLiveData<Boolean>()
    val isDeleting: LiveData<Boolean> get() = _isDeleting
    private val _serverResponse = MutableLiveData<SimpleResponse>()
    val serverResponse : LiveData<SimpleResponse> get() = _serverResponse               //SERVER RESPONSE
    private val _deletionCategory = MutableLiveData<Category>()
    val deletionCategory : LiveData<Category> get() = _deletionCategory     // CATEGORY BEING DELETED
    private val _deletionSubCategory = MutableLiveData<SubCategory>()
    val deletionSubCategory : LiveData<SubCategory> get() = _deletionSubCategory     // SUBCATEGORY BEING DELETED


    fun getCategories(){
        val response = repository.fetchCategory()
        response.enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                print(response.body().toString())

                response.body()?.let {
                    clearList(LiveDataCode.Categories)
                    addAllCategories(it)
                    refreshLiveData(LiveDataCode.Categories)}

            }
            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                print(t.toString())
            }
        })
    }

    fun getSubCategories(id:Int?=null){
        var response : Call<List<SubCategory>>
        if(id==null){
            response = repository.fetchSubCategories()
        }else{
            response = repository.fetchSubcategories(id)
        }

        response.enqueue(object : Callback<List<SubCategory>> {
            override fun onResponse(call: Call<List<SubCategory>>, response: Response<List<SubCategory>>) {
                print(response.body().toString())

                response.body()?.let {
                    clearList(LiveDataCode.SubCategories)
                    addAllSubCategories(it)
                    refreshLiveData(LiveDataCode.SubCategories)}
            }
            override fun onFailure(call: Call<List<SubCategory>>, t: Throwable) {
                print(t.toString())
            }
        })
    }
    fun onDeleteAlert(deleteData : Any){
        when(deleteData){
            is Category -> _deletionCategory.value = deleteData
            is SubCategory -> _deletionSubCategory.value = deleteData
        }
        _isDeleting.value = true
    }
    fun onDeletionCancelOrDone(){
        _isDeleting.value = false
    }

    suspend fun deleteFromDB(category: Category){
        try {
            val response = repository.deleteCategory(category.id!!)
            _serverResponse.postValue(response)
            deleteFromList(category)

        }catch (e : Exception){
            println("$e")
        }
    }
    suspend fun removeSubCategory(subCategory: SubCategory){
        try {
            val response = repository.deleteSubCategory(subCategory.id!!)
            _serverResponse.postValue(response)
            deleteFromList(subCategory)

        }catch (e : Exception){
            println("$e")
        }
    }
    suspend fun addNewCategory(category: Category){
        try {
            val response = repository.insertCategory(category)
            _serverResponse.postValue(response)

        }catch (e : Exception){
            println("$e")
        }
    }
    suspend fun addNewSubCategory(category: SubCategory){
        try {
            val response = repository.insertSubCategory(category)
            _serverResponse.postValue(response)
        }catch (e : Exception){
            println("$e")
        }
    }

    private fun addCategory(category: Category) {
        _categories.value?.add(category)
        refreshLiveData(LiveDataCode.Categories)
    }

    private fun addAllCategories(list : List<Category>){
        _categories.value?.addAll(list)

    }
    private fun addAllSubCategories(list : List<SubCategory>){
        _subCategories.value?.addAll(list)
    }
    private fun clearList(code: LiveDataCode){
        when(code){
            LiveDataCode.Categories -> _categories.value?.clear()
            LiveDataCode.SubCategories -> _subCategories.value?.clear()
        }
    }
    private fun deleteFromList(element : Any){
        when(element){
            is Category -> {
                _categories.value?.remove(element)
                refreshLiveData(LiveDataCode.Categories)
            }
            is SubCategory->{
                _subCategories.value?.remove(element)
                refreshLiveData(LiveDataCode.SubCategories)
            }
        }
    }
    private fun refreshLiveData(code : LiveDataCode){
        when(code){
            LiveDataCode.Categories -> _categories.postValue(_categories.value)
            LiveDataCode.SubCategories -> _subCategories.postValue(_subCategories.value)
        }
    }

}
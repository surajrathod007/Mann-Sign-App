package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsign.data.model.DateFilter
import com.surajmanshal.mannsign.data.model.ordering.Transaction
import com.surajmanshal.mannsign.data.model.ordering.TransactionItem
import com.surajmanshal.mannsign.network.NetworkService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionViewModel : ViewModel() {

    private var _transactions = MutableLiveData<List<Transaction>>()
    val transactions : LiveData<List<Transaction>> get() = _transactions

    private val _transactionItems = MutableLiveData<List<TransactionItem>>()
    val transactionItems : LiveData<List<TransactionItem>> get() = _transactionItems

    private var _msg = MutableLiveData<String>()
    val msg: LiveData<String> get() = _msg

    private var _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    companion object{
        val db = NetworkService.networkInstance
    }

    fun getUserTransactions(email : String,date : DateFilter){
        _isLoading.postValue(true)
        val r = db.getUserTransactions(date,email)
        r.enqueue(object : Callback<List<Transaction>?> {
            override fun onResponse(
                call: Call<List<Transaction>?>,
                response: Response<List<Transaction>?>
            ) {
                val l = response.body()
                val lst = mutableListOf<TransactionItem>()

                if(!l.isNullOrEmpty()){
                    l.forEach {
                        lst.add(TransactionItem(it))
                    }
                }
                _transactionItems.postValue(lst)
                _isLoading.postValue(false)
            }

            override fun onFailure(call: Call<List<Transaction>?>, t: Throwable) {
                _msg.postValue(t.message.toString())
                _isLoading.postValue(false)
            }
        })
    }

    fun getUserAllTransactions(email : String){
        _isLoading.postValue(true)
        val r = db.getUserAllTransaction(email)
        r.enqueue(object : Callback<List<Transaction>?> {
            override fun onResponse(
                call: Call<List<Transaction>?>,
                response: Response<List<Transaction>?>
            ) {
                val l = response.body()
                val lst = mutableListOf<TransactionItem>()

                if(!l.isNullOrEmpty()){
                    l.forEach {
                        lst.add(TransactionItem(it))
                    }
                }
                _transactionItems.postValue(lst)
                _isLoading.postValue(false)
            }

            override fun onFailure(call: Call<List<Transaction>?>, t: Throwable) {
                _msg.postValue(t.message.toString())
                _isLoading.postValue(false)
            }
        })
    }
}
package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.repository.Repository

open class ClientViewModel : ViewModel() {
    protected val repository = Repository()
    protected val _serverResponse = MutableLiveData<SimpleResponse>()
    val serverResponse : LiveData<SimpleResponse> get() = _serverResponse
}
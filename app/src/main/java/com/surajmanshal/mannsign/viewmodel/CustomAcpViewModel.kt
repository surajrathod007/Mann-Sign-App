package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CustomAcpViewModel : ViewModel() {

    private var _currentFontName = MutableLiveData<String>("")
    val currentFontName : LiveData<String> get() = _currentFontName

    private var _currentName = MutableLiveData<String>("Your name")
    val currentName : LiveData<String> get() = _currentName

    fun selectFont(fontName : String){
        _currentFontName.postValue(fontName)
    }

    fun setCurrentName(name : String){
        _currentName.postValue(name)
    }
}
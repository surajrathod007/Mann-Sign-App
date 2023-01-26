package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsign.data.model.ordering.ChatMessage
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.utils.Functions
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatViewModel : ViewModel() {

    private var _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var _chats = MutableLiveData<List<ChatMessage>>(emptyList())
    val chats : LiveData<List<ChatMessage>> get() = _chats

    private var _msg = MutableLiveData<String>()
    val msg: LiveData<String> get() = _msg

    private val _imageUploadResponse = MutableLiveData<SimpleResponse>()
    val imageUploadResponse : LiveData<SimpleResponse> get() = _imageUploadResponse

    val msgSize = MutableLiveData<Int>(0)

    var _scrollDown = MutableLiveData<Boolean>(false)
    val scrollDown : LiveData<Boolean> get() = _scrollDown

    companion object{
        val db = NetworkService.networkInstance
    }

    fun loadChats(oid : String){
        val r = db.loadChats(oid)

        r.enqueue(object : Callback<List<ChatMessage>?> {
            override fun onResponse(
                call: Call<List<ChatMessage>?>,
                response: Response<List<ChatMessage>?>
            ) {
                if(!response.body().isNullOrEmpty())
                {
                    _chats.postValue(response.body())
                    msgSize.postValue(response.body()!!.size)
                }else{
                    _msg.postValue("Response is null")
                }
            }
            override fun onFailure(call: Call<List<ChatMessage>?>, t: Throwable) {
                _msg.postValue(t.message.toString())
            }
        })
    }

    fun addChat(msg : ChatMessage,done : () -> Unit = {}){
        val r = db.addChat(msg)
//        try{
//            val l = _chats.value?.toMutableList()
//            l?.add(msg)
//            _chats.postValue(l)
//        }catch (e : Exception){
//            _msg.postValue(e.message.toString())
//        }

        r.enqueue(object : Callback<SimpleResponse?> {
            override fun onResponse(
                call: Call<SimpleResponse?>,
                response: Response<SimpleResponse?>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.success){
                        //loadChats(msg.orderId)
                        _scrollDown.postValue(true)
                        done.invoke()
                    }
                    _msg.postValue(response.body()?.message)
                }
            }

            override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                _msg.postValue(t.message.toString())
            }
        })
    }

    suspend fun uploadChatImage(part: MultipartBody.Part,chat : ChatMessage){
        try {
            _isLoading.postValue(true)
            val response = db.uploadChatImage(part)
            if(response.success){
                chat.also {
                    it.imageUrl = response.message
                }
                val j = db.addImageChat(chat)
                _msg.postValue(j.message)
                _isLoading.postValue(false)
            }else{
                _msg.postValue(response.message)
                _isLoading.postValue(false)
            }


        }catch (e : Exception){
            _msg.postValue("Exception : "+e.message.toString())
            _isLoading.postValue(false)
        }
    }

    fun addImageChat(part: MultipartBody.Part,chat : ChatMessage){
        _isLoading.postValue(true)
        val r = db.addChatImage(part,chat)
        r.enqueue(object : Callback<SimpleResponse?> {
            override fun onResponse(
                call: Call<SimpleResponse?>,
                response: Response<SimpleResponse?>
            ) {
                _msg.postValue(response.body()?.message.toString())
                _isLoading.postValue(false)
            }

            override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                _msg.postValue("Exception ${t.message}")
                _isLoading.postValue(true)
            }
        })
    }

}
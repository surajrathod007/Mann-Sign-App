package com.surajmanshal.mannsign.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surajmanshal.mannsign.data.model.ordering.ChatMessage
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.network.NetworkService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatViewModel : ViewModel() {

    private var _chats = MutableLiveData<List<ChatMessage>>()
    val chats : LiveData<List<ChatMessage>> get() = _chats

    private var _msg = MutableLiveData<String>()
    val msg: LiveData<String> get() = _msg

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
                }else{
                    _msg.postValue("Response is null")
                }
            }

            override fun onFailure(call: Call<List<ChatMessage>?>, t: Throwable) {
                _msg.postValue(t.message.toString())
            }
        })
    }

    fun addChat(msg : ChatMessage){
        val r = db.addChat(msg)
        r.enqueue(object : Callback<SimpleResponse?> {
            override fun onResponse(
                call: Call<SimpleResponse?>,
                response: Response<SimpleResponse?>
            ) {
                if(response.isSuccessful){
                    if(response.body()!!.success){
                        loadChats(msg.orderId)
                    }
                    _msg.postValue(response.body()?.message)
                }
            }

            override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                _msg.postValue(t.message.toString())
            }
        })
    }

}
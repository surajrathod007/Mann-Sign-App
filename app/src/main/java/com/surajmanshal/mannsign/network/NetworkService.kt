package com.surajmanshal.mannsign.network


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.surajmanshal.mannsign.URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkService {
    val networkInstance2: NetworkCallsInterface2
    val networkInstance : NetworkCallsInterface
    init {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .build()
        val retrofit = Retrofit.Builder().baseUrl(URL.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val retrofit2 = Retrofit.Builder().baseUrl("https://api.phonepe.com/apis/hermes/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        networkInstance = retrofit.create(NetworkCallsInterface::class.java)
        networkInstance2 = retrofit2.create(NetworkCallsInterface2::class.java)
    }
    fun checkForInternet(context : Context):Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo?=connectivityManager.activeNetworkInfo
        return if(activeNetwork?.isConnected!=null){
            activeNetwork.isConnected
        }else{
            false
        }
    }
}
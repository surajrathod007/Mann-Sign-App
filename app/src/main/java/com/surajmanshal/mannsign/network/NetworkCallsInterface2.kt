package com.surajmanshal.mannsign.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface NetworkCallsInterface2 {
    @POST("pg/v1/pay")
    fun makePayment(@Header("X-VERIFY") checkSum : String, @Body request: PayAPIRequestObject, @Header("Content-Type") type : String = "application/json") : Call<PG_PAY_Response>
}

data class PayAPIRequestObject(
    val request : String
)

data class PG_PAY_Response(
    val code: String,
    val data: Data,
    val message: String,
    val success: Boolean
)

data class Data(
    val instrumentResponse: InstrumentResponse,
    val merchantId: String,
    val merchantTransactionId: String
)

data class InstrumentResponse(
    val intentUrl: String,
    val type: String
)
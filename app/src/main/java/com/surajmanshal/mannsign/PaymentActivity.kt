package com.surajmanshal.mannsign

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.surajmanshal.mannsign.data.model.payment.InitiateTxnRequest
import com.surajmanshal.mannsign.data.model.payment.PhonePePayLoad
import com.surajmanshal.mannsign.databinding.ActivityPaymentBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.ui.fragments.PhonePeFragment
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.auth.LoadingScreen
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentActivity : AppCompatActivity() {
    lateinit var binding: ActivityPaymentBinding
    val loadingScreen by lazy {
        LoadingScreen(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PhonePe.init(this)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer,PhonePeFragment())
            .commit()
    }

    fun initiatePayment(selectedApp: String,onInitiated : (Boolean) -> Unit) {
        // Replace with your payment logic
        lifecycleScope.launch {
            NetworkService.networkInstance.getPhonePePayload(
                InitiateTxnRequest(
                    selectedApp,"txnFirst${System.currentTimeMillis()}",200, Constants.MANN_SIGN_PHONE_NUMBER
                )
            )
                .enqueue(object : Callback<PhonePePayLoad?> {
                    override fun onResponse(
                        call: Call<PhonePePayLoad?>,
                        response: Response<PhonePePayLoad?>
                    ) {
                        response.body()?.let { it ->
                            println("$it")
                            onInitiated(it.simpleResponse.success)
                            if (it.simpleResponse.success) {
                                // SDK Less
                                /*lifecycleScope.launch {
                                    NetworkService.networkInstance2.makePayment(
                                        it.checksum!!,
                                        PayAPIRequestObject(it.base64Payload!!)
                                    ).enqueue(object : Callback<PG_PAY_Response> {
                                        override fun onResponse(
                                            call: Call<PG_PAY_Response>,
                                            response: Response<PG_PAY_Response>
                                        ) {
                                            println(response.body()?.message)
                                            response.body()?.let {
                                                println("$it")
                                                it.apply {
                                                    println("$this")
                                                    val redirectUrl = data.instrumentResponse.intentUrl
                                                    println(redirectUrl)
                                                    val intent = Intent().apply {
                                                        action = Intent.ACTION_VIEW
                                                        data = Uri.parse(redirectUrl) // PhonePe Intent redirectUrl from the response.
                                                        setPackage(selectedApp) // selectedApp will be the package name of the App selected by the user.
                                                    }
                                                    startActivityForResult(intent, B2B_PG_REQUEST_CODE)
                                                }
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<PG_PAY_Response>,
                                            t: Throwable
                                        ) {
                                            println("$t")
                                        }
                                    })
                                }*/
                                // SDK
                                makeB2BReq(it.base64Payload!!,it.checksum!!,selectedApp)
                            }
                        }
                    }

                    override fun onFailure(call: Call<PhonePePayLoad?>, t: Throwable) {
                        println("$t")
                        onInitiated(false)
                    }
                })
        }
    }

    fun makeB2BReq(base64Payload : String,checksum : String,pkg : String){

        val b2BPGRequest = B2BPGRequestBuilder()
            .setData(base64Payload)
            .setChecksum(checksum)
            .setUrl("/pg/v1/pay")
            .build()
        val i = PhonePe.getImplicitIntent(
            this@PaymentActivity,
            b2BPGRequest,
            pkg
        )
        startActivityForResult(
            i!!,B2B_PG_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == B2B_PG_REQUEST_CODE) {
            Toast.makeText(this, "Payment Flow Complete", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "Check Payment Status", Toast.LENGTH_SHORT).show()
            // Handle payment completion UI callback
            // Inform your server to check the payment status
        }
    }

    companion object {
        const val B2B_PG_REQUEST_CODE = 79
    }
}
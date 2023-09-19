package com.surajmanshal.mannsign.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.surajmanshal.mannsign.PaymentActivity
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.databinding.FragmentCheckPaymentStatusBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.utils.hide
import com.surajmanshal.mannsign.utils.show
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckPaymentStatusFragment : Fragment() {
    lateinit var binding: FragmentCheckPaymentStatusBinding
    lateinit var paymentActivity : PaymentActivity
    private lateinit var orderId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentActivity = requireActivity() as PaymentActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCheckPaymentStatusBinding.inflate(layoutInflater)
        NetworkService.networkInstance.getPaymentStatus(orderId)
            .enqueue(object : Callback<SimpleResponse?> {
                override fun onResponse(
                    call: Call<SimpleResponse?>,
                    response: Response<SimpleResponse?>
                ) {
                    response.body()?.let {
                        if(it.success){
                            binding.animSuccess.apply {
                                show()
                                playAnimation()
                            }
                            CoroutineScope(Dispatchers.IO).launch {
                                delay(3000)
                                withContext(Dispatchers.Main) {
                                    binding.progressLayout.hide()
                                    binding.resultLayout.show()
                                    binding.btnComplete.setOnClickListener {
                                        requireActivity().finish()
                                    }
                                }
                            }
                        }else{
                            Toast.makeText(requireContext(), "Payment Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                        false
                    }?:run{
                        Toast.makeText(requireContext(), "Null res", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                    throw t
                }
            })
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param orderId : Required to check status of corresponding Payment.
         * @return A new instance of fragment CheckPaymentStatusFragment.
         */
        @JvmStatic
        fun newInstance(orderId : String) =
            CheckPaymentStatusFragment().apply {
                this.orderId = orderId
            }
    }
}
package com.surajrathod.authme.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.databinding.FragmentForgotPasswordBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.utils.auth.ExceptionHandler
import com.surajmanshal.mannsign.utils.auth.LoadingScreen
import kotlinx.coroutines.launch

class ForgotPasswordFragment : Fragment() {
    private lateinit var binding : FragmentForgotPasswordBinding
    lateinit var d : LoadingScreen
    lateinit var dd : Dialog

    var email : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)
        binding = FragmentForgotPasswordBinding.bind(view)
         d = LoadingScreen(activity as Context)
         dd = d.loadingScreen()
        binding.btnReset.setOnClickListener {
            if(!TextUtils.isEmpty(binding.ETEmail.text.toString().trim(){it <= ' ' })){
                d.toggleDialog(dd)
                    lifecycleScope.launch{
                        try {
                            email = binding.ETEmail.text.toString()
                            val response = NetworkService.networkInstance.sendOtp(email!!)
                            onSimpleResponse("Sent",response)
                        }catch (e : Exception){
                            activity?.let { ExceptionHandler.catchOnContext(it, e.toString()) }
                            d.toggleDialog(dd)
                        }
                    }
            }else{
                Toast.makeText(activity, "please enter email address", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
    fun onSimpleResponse(task : String,simpleResponse: SimpleResponse){
        if(simpleResponse.success){
            d.toggleDialog(dd)  // hide
            Toast.makeText(activity, "$task Successful", Toast.LENGTH_SHORT).show()
            val bundle = Bundle()
            bundle.putString("email",email)
            bundle.putString("otp",simpleResponse.message)
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_resetPasswordFragment,bundle)
        }else{
            d.toggleDialog(dd)  // hide
            activity?.let { ExceptionHandler.catchOnContext(it,simpleResponse.message) }
        }
    }
}
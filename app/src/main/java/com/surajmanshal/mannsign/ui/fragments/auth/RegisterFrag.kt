package com.surajmanshal.mannsign.ui.fragments.auth


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.onesignal.OneSignal
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.data.model.auth.User
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.databinding.FragRegisterBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.utils.auth.ExceptionHandler
import com.surajmanshal.mannsign.utils.auth.LoadingScreen
import kotlinx.coroutines.launch

class RegisterFrag : Fragment() {

    lateinit var binding: FragRegisterBinding
    lateinit var d : LoadingScreen
    lateinit var dd : Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.frag_register, container, false)
        binding = FragRegisterBinding.bind(view)
        // Loader
        d = LoadingScreen(activity as Context)
        dd = d.loadingScreen()

        // Views Setup
        val etEmail = binding.ETEmail
        var etPassword = binding.ETPassword
        val etMobileNo = binding.etMobileNumber

        //Functions.makeToast(requireContext(),OneSignal.getDeviceState()?.userId.toString())

        // Event Listeners
        binding.tvLoginHere.setOnClickListener {
            findNavController().navigate(R.id.action_registerFrag_to_loginFrag)
        }
        binding.btnRegister.setOnClickListener {
            try{
                OneSignal.setExternalUserId(etEmail.text.toString())
            }catch (e : Exception){
                Functions.makeToast(requireContext(),e.message.toString())
            }

            Functions.makeToast(requireContext(),OneSignal.getDeviceState()?.userId.toString())
            val did = OneSignal.getDeviceState()?.userId
            if(!isDataFillled(etEmail))else if(!isDataFillled(etPassword))else if(!isDataFillled(etMobileNo))else{
                registerUser(User().apply {
                    emailId = etEmail.text.toString()
                    password = etPassword.text.toString()
                    phoneNumber = etMobileNo.text.toString()
                    deviceId = did
                })
            }
        }

        return view
    }
    private fun isDataFillled(view: TextView) : Boolean{
        if (TextUtils.isEmpty(view.text.toString().trim() { it <= ' ' })) {
            when(view){
                binding.ETEmail -> Snackbar.make(view, "Email is required", 1000).show()
                binding.ETPassword -> Snackbar.make(view, "Password is required", 1000).show()
                binding.etMobileNumber -> Snackbar.make(view, "Mobile number is required", 1000).show()
            }
            return false
        }
        return true
    }
    private fun registerUser(user: User){
        d.toggleDialog(dd)  // show
       lifecycleScope.launch {
           try{
               val register = NetworkService.networkInstance.registerUser(user)
               onSimpleResponse("Registration",register)
           }catch (e : Exception){
               activity?.let { ExceptionHandler.catchOnContext(it, " ${e.message}Email already registered") }
               d.toggleDialog(dd)
           }
       }
    }
    private fun onSimpleResponse(task:String, simpleResponse: SimpleResponse){
        if(simpleResponse.success){
            d.toggleDialog(dd)  // hide
            Toast.makeText(activity, "$task Successful", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_registerFrag_to_loginFrag)
        }else{
            d.toggleDialog(dd)  // hide
            Toast.makeText(activity, simpleResponse.message, Toast.LENGTH_SHORT).show()
        }
    }
}


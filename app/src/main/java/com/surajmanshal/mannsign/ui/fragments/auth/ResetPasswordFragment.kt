package com.surajmanshal.mannsign.ui.fragments.auth

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.databinding.FragmentResetPasswordBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.utils.auth.ExceptionHandler
import com.surajmanshal.mannsign.utils.auth.GenericTextWatcher
import com.surajmanshal.mannsign.utils.auth.LoadingScreen
import com.surajrathod.authme.util.GetInput
import kotlinx.coroutines.launch

class ResetPasswordFragment : Fragment() {
    lateinit var otp_textbox_one : EditText
    lateinit var otp_textbox_two : EditText
    lateinit var otp_textbox_three:EditText
    lateinit var otp_textbox_four:EditText
    lateinit var verify_otp: Button
    lateinit var binding : FragmentResetPasswordBinding
    lateinit var edit : Array<EditText>
    lateinit var password : EditText
    lateinit var confirmPassword : EditText

    lateinit var d : LoadingScreen
    lateinit var dd : Dialog

    var email : String? = null
    var otp : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_reset_password, container, false)
        binding = FragmentResetPasswordBinding.bind(view)
        d = LoadingScreen(activity as Context)
        dd = d.loadingScreen()

        with(view){
            otp_textbox_one = findViewById(R.id.etOtp1)
            otp_textbox_two = findViewById(R.id.etOtp2)
            otp_textbox_three = findViewById(R.id.etOtp3)
            otp_textbox_four = findViewById(R.id.etOtp4)
            verify_otp = findViewById(R.id.btnReset)
            password = findViewById(R.id.etPassword)
            confirmPassword = findViewById(R.id.etConfirmPassword)
        }


         edit =
            arrayOf<EditText>(otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four)

        otp_textbox_one.addTextChangedListener(GenericTextWatcher(otp_textbox_one, edit))
        otp_textbox_two.addTextChangedListener(GenericTextWatcher(otp_textbox_two, edit))
        otp_textbox_three.addTextChangedListener(GenericTextWatcher(otp_textbox_three, edit))
        otp_textbox_four.addTextChangedListener(GenericTextWatcher(otp_textbox_four, edit))

        password.apply {

            fun setUpHelper(){
                binding.etPasswordContainer.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                binding.etPasswordContainer.helperText = validPassword()
            }
            setOnFocusChangeListener { _, focused ->
                if (!focused) setUpHelper()
            }
            doOnTextChanged { _, _, _, _ ->
                setUpHelper()
            }
        }


        verify_otp.setOnClickListener {
            if(!isDataFillled(password)) {
                ExceptionHandler.catchOnContext(requireContext(),"Fill the missing fields")
                return@setOnClickListener
            }
            if (!binding.etPasswordContainer.helperText.isNullOrEmpty()) {
                ExceptionHandler.catchOnContext(requireContext(),"Password must be valid")
                return@setOnClickListener
            }
            if(password.text.toString()!=confirmPassword.text.toString()){
                ExceptionHandler.catchOnContext(requireContext(),"Recheck Both Password")
                return@setOnClickListener
            }

            if(isEnteredOtp()){
                email = arguments?.get("email") as String?
                if(otp != arguments?.get("otp")as String?){
                    Toast.makeText(requireContext(), "Wrong OTP", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                d.toggleDialog(dd)
                verifyAndResetPassword()
            }else{
                ExceptionHandler.catchOnContext(requireContext(),"Please enter OTP")
            }
        }
        return view
    }
    fun isDataFillled(view: EditText) : Boolean{
        /*if (TextUtils.isEmpty(view.text.toString().trim() { it <= ' ' })) {
            Snackbar.make(view, "Fields are empty", 1000).show()
            return false
        }
        return true*/
        return view.text.isNotBlank()
    }

    private fun validPassword(): String? {
        val passwordText = binding.etPassword.text.toString()
        if (passwordText.length < 8) {
            return "Minimum 8 character required for password"
        }
        if (!passwordText.matches(".*[A-Z].*".toRegex())) {
            return "Must contain 1 Upper-case character"
        }
        if (!passwordText.matches(".*[a-z].*".toRegex())) {
            return "Must contain 1 Lower-case character"
        }
        if (!passwordText.matches(".*[@#\$%^&+=].*".toRegex())) {
            return "Must contain 1 Special character (@#\$%^&+=)"
        }
        return null
    }
    fun isEnteredOtp() : Boolean{
        if(otp_textbox_one.text.isEmpty()) return false
        if(otp_textbox_two.text.isEmpty()) return false
        if(otp_textbox_three.text.isEmpty()) return false
        if(otp_textbox_four.text.isEmpty()) return false
        otp=""
       edit.forEach {
           otp+= GetInput.takeFrom(it)
       }
        return true
    }
    fun verifyAndResetPassword(){
        lifecycleScope.launch{
            try {
                val newpass = confirmPassword.text.toString()
                val response = NetworkService.networkInstance.resetPassword(email!!,newpass)
                onSimpleResponse("Reset",response)
            }catch (e : Exception){
                d.toggleDialog(dd)
                activity?.let {
                    activity?.let { ExceptionHandler.catchOnContext(it,"Wrong OTP") }
                }
            }
        }
    }
    fun onSimpleResponse(task : String,simpleResponse: SimpleResponse){
        if(simpleResponse.success){
            d.toggleDialog(dd)  // hide
            Toast.makeText(activity, "$task Successful", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_resetPasswordFragment_to_loginFrag)
        }else{
            d.toggleDialog(dd)  // hide
            activity?.let { ExceptionHandler.catchOnContext(it,simpleResponse.message) }
        }
    }
}
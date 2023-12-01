package com.surajmanshal.mannsign.ui.fragments.auth


import android.animation.LayoutTransition
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.onesignal.OneSignal
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.customviews.MorphButton
import com.surajmanshal.mannsign.customviews.dp
import com.surajmanshal.mannsign.customviews.getColorX
import com.surajmanshal.mannsign.customviews.getDrawableX
import com.surajmanshal.mannsign.customviews.sp
import com.surajmanshal.mannsign.data.model.auth.User
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.databinding.FragRegisterBinding
import com.surajmanshal.mannsign.databinding.FragmentPolicyPageBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.utils.auth.ExceptionHandler
import com.surajmanshal.mannsign.utils.auth.LoadingScreen
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFrag : Fragment() {

    lateinit var binding: FragRegisterBinding
    lateinit var d: LoadingScreen
    lateinit var dd: Dialog

    //states , can be managed in viewmodel ;)
    var emailVerified = false
    var myOtp = ""
    var otpSent = false
    //TODO: MAke a new otp sent function on server side

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.frag_register, container, false)
        binding = FragRegisterBinding.bind(view)
        binding.llRegisterMain.layoutTransition.enableTransitionType(LayoutTransition.CHANGE_APPEARING)
        // Loader
        d = LoadingScreen(activity as Context)
        dd = d.loadingScreen()

        // Views Setup
        val etEmail = binding.ETEmail
        var etPassword = binding.ETPassword
        val etMobileNo = binding.etMobileNumber

        //Functions.makeToast(requireContext(),OneSignal.getDeviceState()?.userId.toString())

        // Event Listeners

        emailFocusListner()
        passwordFocusListner()
        phoneFocusListner()
        textWatchers()

        binding.btnVerifyEmail.apply {
            fromBgColor = getColorX(R.color.green)
            toBgColor = getColorX(R.color.green)
            fromTextColor = getColorX(R.color.white)
            toTextColor = getColorX(R.color.black)
            text = "Send Otp"
            textSize = 16 * sp()
            setPadding(
                (32 * dp()).toInt(),
                (16 * dp()).toInt(),
                (32 * dp()).toInt(),
                (16 * dp()).toInt()
            )
            iconDrawable = getDrawableX(R.drawable.ic_sync).apply {
                setTint(getColorX(R.color.white))
            }
        }
        binding.tvLoginHere.setOnClickListener {
            findNavController().navigate(R.id.action_registerFrag_to_loginFrag)
        }
        binding.btnVerifyEmail.setOnClickListener {
            verifyEmail()
        }
        binding.btnRegister.setOnClickListener {
            try {
                OneSignal.setExternalUserId(etEmail.text.toString())
            } catch (e: Exception) {
                Functions.makeToast(requireContext(), e.message.toString())
            }

            //Functions.makeToast(requireContext(), OneSignal.getDeviceState()?.userId.toString())
            val did = OneSignal.getDeviceState()?.userId
            if (!isDataFillled(etEmail)) else if (!isDataFillled(etPassword)) else if (!isDataFillled(
                    etMobileNo
                )
            ) else if (!validation()) else {
                registerUser(User().apply {
                    emailId = etEmail.text.toString()
                    password = etPassword.text.toString()
                    phoneNumber = etMobileNo.text.toString()
                    deviceId = did
                })
            }
        }

        val strPrivacyPolicy = "Privacy Policy"
        val strTermsAndConditions = "Terms & Conditions"
        val agreement = "By Signing Up you agree to MannSign $strTermsAndConditions and $strPrivacyPolicy"
        val tncStart = agreement.indexOf(strTermsAndConditions)
        val privacyStart = agreement.indexOf(strPrivacyPolicy)
        val txtSpannaleString = SpannableString(agreement).apply{

            fun showPolicyDialog(url:String){
                Dialog(requireContext()).apply {

                    val binding = FragmentPolicyPageBinding.bind(
                        inflater.inflate(
                            R.layout.fragment_policy_page,
                            container,
                            false
                        )
                    )
                    binding.contentView.loadUrl(url)
                    setContentView(
                        binding.root
                    )
                    show()
                }
            }

            setSpan(
                object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        showPolicyDialog(Constants.URL_TERMS_OF_SERVICE)
                    }
                    override fun updateDrawState(txtPaint: TextPaint) {
                        super.updateDrawState(txtPaint)
                        txtPaint.isUnderlineText = false
                        txtPaint.color = AppCompatResources.getColorStateList(requireContext(),R.color.buttonColor).defaultColor
                    }
                },tncStart,tncStart+strTermsAndConditions.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        showPolicyDialog(Constants.URL_PRIVACY_POLICY)
                    }
                    override fun updateDrawState(txtPaint: TextPaint) {
                        super.updateDrawState(txtPaint)
                        txtPaint.isUnderlineText = false
                        txtPaint.color = AppCompatResources.getColorStateList(requireContext(),R.color.buttonColor).defaultColor
                    }
                },privacyStart,privacyStart+strPrivacyPolicy.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.tvTAndC.apply {
            text = txtSpannaleString
            movementMethod = LinkMovementMethod.getInstance()
        }

        return view
    }

    private fun verifyEmail() {
        if (!binding.edEmailContainer.helperText.isNullOrEmpty()) {
            Snackbar.make(binding.ETEmail, "Email must be valid", 1000).show()
        } else if (otpSent) {
            if (!binding.edOtp.text.isNullOrEmpty()) {
                if (binding.edOtp.text.toString() == myOtp) {
                    emailVerified = true
                    binding.llVerifierLayout.visibility = View.GONE
                    binding.txtOtpMessage.text = "Your email is verified ! "
                    binding.txtOtpMessage.setTextColor(requireContext().resources.getColor(R.color.normal_button_color))
                    binding.ETEmail.isEnabled = false
                    Toast.makeText(requireContext(), "Email verified", Toast.LENGTH_SHORT).show()
                } else {
                    emailVerified = false
                    Toast.makeText(requireContext(), "Wrong otp", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please enter otp", Toast.LENGTH_SHORT).show()
            }

        } else {

            binding.btnVerifyEmail.setUIState(MorphButton.UIState.Loading)
            binding.btnVerifyEmail.isEnabled = false
            Functions.makeToast(requireContext(), "Sending Otp please wait...")
            val r =
                NetworkService.networkInstance.sendOtpNew(binding.ETEmail.text?.trim().toString())
            r.enqueue(object : Callback<SimpleResponse?> {
                override fun onResponse(
                    call: Call<SimpleResponse?>,
                    response: Response<SimpleResponse?>
                ) {
                    //Functions.makeToast(requireContext(),"${response.body()?.message}")
                    binding.txtOtpMessage.visibility = View.VISIBLE
                    binding.btnVerifyEmail.apply {
                        text = "Verify"
                        setUIState(MorphButton.UIState.Button)
                    }
                    myOtp = response.body()?.message.toString()
                    otpSent = true
                    binding.btnVerifyEmail.isEnabled = true
                }

                override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                    Functions.makeToast(requireContext(), "${t.message}")
                    binding.btnVerifyEmail.apply {
                        text = "Send otp"
                        setUIState(MorphButton.UIState.Button)
                        binding.btnVerifyEmail.isEnabled = true
                    }
                }
            })

        }
    }

    private fun emailFocusListner() {
        binding.ETEmail.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.edEmailContainer.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                binding.edEmailContainer.helperText = validEmail()
            }
        }
    }

    private fun passwordFocusListner() {
        binding.ETPassword.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.edPasswordContainer.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                binding.edPasswordContainer.helperText = validPassword()
            }
        }
    }

    private fun phoneFocusListner() {
        binding.etMobileNumber.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.edPhoneContainer.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                binding.edPhoneContainer.helperText = validPhone()
            }
        }
    }

    private fun validPassword(): String? {
        val passwordText = binding.ETPassword.text.toString()
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

    /**
     * Validates if it a not a fake number or invalid number
     */
    private fun validPhone(): String? {
        val phoneText = binding.etMobileNumber.text.toString()
        if (phoneText.length != 10) {
            return "Must be 10 digit phone number"
        }
        if (phoneText == "0123456789" ){
            return "Invalid phone number"
        }
        if (phoneText == "1234567890" ){
            return "Invalid phone number"
        }
        val allSameDigits = phoneText.all { it == phoneText[0] }
        if (allSameDigits){
            return "Invalid phone number"
        }
        if (!phoneText.matches(Regex("^[6-9]([0-9]{9})"))) {
            return "Invalid phone number"
        }
        return null
    }

    private fun validEmail(): String? {
        val emailText = binding.ETEmail.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return "Invalid email address"
        }
        return null
    }

    private fun isDataFillled(view: TextView): Boolean {
        if (TextUtils.isEmpty(view.text.toString().trim() { it <= ' ' })) {
            when (view) {
                binding.ETEmail -> Snackbar.make(view, "Email is required", 1000).show()
                binding.ETPassword -> Snackbar.make(view, "Password is required", 1000).show()
                binding.etMobileNumber -> Snackbar.make(view, "Mobile number is required", 1000)
                    .show()
            }
            return false
        }
        return true
    }


    private fun registerUser(user: User) {
        d.toggleDialog(dd)  // show
        lifecycleScope.launch {
            try {
                val register = NetworkService.networkInstance.registerUser(user)
                onSimpleResponse("Registration", register)
            } catch (e: Exception) {
                activity?.let {
                    ExceptionHandler.catchOnContext(
                        it,
                        " ${e.message}Email already registered"
                    )
                }
                d.toggleDialog(dd)
            }
        }
    }

    private fun onSimpleResponse(task: String, simpleResponse: SimpleResponse) {
        if (simpleResponse.success) {
            d.toggleDialog(dd)  // hide
            Toast.makeText(activity, "$task Successful", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_registerFrag_to_loginFrag)
        } else {
            d.toggleDialog(dd)  // hide
            Toast.makeText(activity, simpleResponse.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun textWatchers() {
        binding.ETEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.edEmailContainer.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                binding.edEmailContainer.helperText = validEmail()
                if (!emailVerified) {
                    if (!p0.isNullOrEmpty()) {
                        binding.llVerifierLayout.visibility = View.VISIBLE
                    } else {
                        binding.llVerifierLayout.visibility = View.GONE
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.ETPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.edPasswordContainer.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                binding.edPasswordContainer.helperText = validPassword()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.etMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.edPhoneContainer.setHelperTextColor(ColorStateList.valueOf(Color.RED))
                binding.edPhoneContainer.helperText = validPhone()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    fun validation(): Boolean {
        if (!binding.edEmailContainer.helperText.isNullOrEmpty()) {
            Snackbar.make(binding.ETEmail, "Email must be valid", 1000).show()
            return false
        }

        if (!binding.edPhoneContainer.helperText.isNullOrEmpty()) {
            Snackbar.make(binding.etMobileNumber, "Phone number must be valid", 1000).show()
            return false
        }

        if (!binding.edPasswordContainer.helperText.isNullOrEmpty()) {
            Snackbar.make(binding.ETPassword, "Password must be valid", 1000).show()
            return false
        }

        if (!emailVerified) {
            Snackbar.make(binding.ETPassword, "Please verify email !", 1000).show()
            return false
        }
        return true
    }
}


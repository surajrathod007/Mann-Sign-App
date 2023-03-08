package com.surajmanshal.mannsign.ui.fragments.auth

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.onesignal.OneSignal
import com.surajmanshal.mannsign.MainActivity
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.data.model.auth.LoginReq
import com.surajmanshal.mannsign.data.model.auth.LoginResponse
import com.surajmanshal.mannsign.data.model.auth.User
import com.surajmanshal.mannsign.databinding.FragLoginBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.room.LocalDatabase
import com.surajmanshal.mannsign.room.user.UserEntity
import com.surajmanshal.mannsign.utils.auth.DataStore
import com.surajmanshal.mannsign.utils.auth.DataStore.preferenceDataStoreAuth
import com.surajmanshal.mannsign.utils.auth.ExceptionHandler
import com.surajmanshal.mannsign.utils.auth.LoadingScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFrag : Fragment() {
    lateinit var binding : FragLoginBinding
    lateinit var d : LoadingScreen
    lateinit var dd : Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.frag_login, container, false)
        binding = FragLoginBinding.bind(view)
        d = LoadingScreen(activity as Context)
        dd = d.loadingScreen()
        val email = binding.ETEmail
        val password = binding.ETPassword
        with(binding){
            TVRegister.setOnClickListener { findNavController().navigate(R.id.action_loginFrag_to_registerFrag) }
            TVForgotPassword.setOnClickListener { findNavController().navigate(R.id.action_loginFrag_to_forgotPasswordFragment) }
            BtnLogin.setOnClickListener {
                 if(!isDataFillled(email))else if(!isDataFillled(password))else{
                    loginUser(email.text.toString(),password.text.toString())
                }
            }
        }
        return view
    }
    fun isDataFillled(view: TextView) : Boolean{
        if (TextUtils.isEmpty(view.text.toString().trim() { it <= ' ' })) {
            when(view){
                binding.ETEmail -> Snackbar.make(view, "Email is required", 1000).show()
                binding.ETPassword -> Snackbar.make(view, "Password is required", 1000).show()
            }
            return false
        }
        return true
    }
    fun loginUser(email : String,password : String){
        d.toggleDialog(dd) //show

       lifecycleScope.launch {
           var response : LoginResponse? = null
           try {
                response =  NetworkService.networkInstance.loginUser(LoginReq(email,password,OneSignal.getDeviceState()!!.userId))
              }catch (e:Exception){
               activity?.let { ExceptionHandler.catchOnContext(it, getString(R.string.generalErrorMsg)) }
               d.toggleDialog(dd)
              }
           if(response!=null){
               if(response.simpleResponse.success){
                   onSimpleResponse("Login",response.user)
                   Toast.makeText(requireContext(), response.simpleResponse.message, Toast.LENGTH_SHORT).show()
               }else{
                   ExceptionHandler.catchOnContext(requireContext(), response.simpleResponse.message)
                   d.toggleDialog(dd)
               }
           }else Toast.makeText(activity, "Null Received", Toast.LENGTH_SHORT).show()
       }
    }
    fun onSimpleResponse(task : String, user: User){
        d.toggleDialog(dd)  // hide
        CoroutineScope(Dispatchers.IO).launch{
            storeStringPreferences(DataStore.JWT_TOKEN,user.token)
        }

        val sharedPreference =  requireActivity().getSharedPreferences("user_e",Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("email",user.emailId)
        editor.putString("token",user.token)
        editor.commit()

        val intent = Intent(requireActivity(), MainActivity::class.java)

        val u = UserEntity(
            emailId = user.emailId,
//            token = user.token
        )
        val db = LocalDatabase.getDatabase(this.requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            db.userDao().insertUser(u)
        }

        startActivity(intent)
        activity?.finish()

    }
    suspend fun storeStringPreferences(key: String ,value : String){
        requireActivity().preferenceDataStoreAuth.edit {
                it[stringPreferencesKey(key)] = value
        }
    }
}
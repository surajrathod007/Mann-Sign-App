package com.surajmanshal.mannsign.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import com.surajmanshal.mannsign.AuthenticationActivity
import com.surajmanshal.mannsign.ProfileEdit
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.data.model.auth.User
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.databinding.FragmentUserProfileBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.room.UserDao
import com.surajmanshal.mannsign.room.UserDatabase
import com.surajmanshal.mannsign.room.UserEntity
import com.surajmanshal.mannsign.ui.activity.OrdersActivity
import com.surajmanshal.mannsign.ui.activity.TransactionsActivity
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.utils.Functions.makeToast
import com.surajmanshal.mannsign.utils.auth.DataStore
import com.surajmanshal.mannsign.utils.auth.DataStore.preferenceDataStoreAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//TODO : Remove static text values in xml file for firstname etc.
class UserProfileFragment(var token: String?) : Fragment() {

    lateinit var binding : FragmentUserProfileBinding
    lateinit var userDatabase : UserDao
    var mUser : User = User()
    var email : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)
        binding = FragmentUserProfileBinding.bind(view)

        val sharedPreferences = activity?.getSharedPreferences("user_e", Context.MODE_PRIVATE)
        email = sharedPreferences?.getString("email", "")

        if(token.isNullOrEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                token = getToken(DataStore.JWT_TOKEN)
            }
        }
        if(!email.isNullOrEmpty()){
            userDatabase = UserDatabase.getDatabase(requireContext()).userDao()
            val user = userDatabase.getUser(email!!)
            setupUserDetails(user)
        }else{
            binding.btnLogoutFrag.setTextColor(R.color.order_selected_text_color)
            binding.btnLogoutFrag.text = "Login/Register"
            makeToast(requireContext(),"Please Login")
        }

        setupClickListeners()
        return binding.root
    }

    private fun setupClickListeners() {
        with(binding){
            btnMyOrdersFrag.setOnClickListener {
                startActivity(Intent(requireActivity(), OrdersActivity::class.java))
            }
            btnMyTransactionsFrag.setOnClickListener {
                startActivity(Intent(requireActivity(), TransactionsActivity::class.java))
            }
            btnEditProfileFrag.setOnClickListener {
                startActivity(Intent(requireActivity(), ProfileEdit::class.java))
            }
            btnLogoutFrag.setOnClickListener {
                if(!email.isNullOrEmpty())
                {
                    //bottomMenu.dismiss()
                    val d = AlertDialog.Builder(requireContext())
                    d.setTitle("Do you want to logout ?")
                    d.setMessage("You can login anytime ;)")
                    d.setPositiveButton("Yes"){v,m ->
                        try{
                            if(!token.isNullOrEmpty()){
                                logout(email!!, token!!)
                            }else{
                                makeToast(requireContext(),"JWT token is empty : You never logged in")
                            }
                        }catch (e : Exception){
                            Functions.makeToast(requireContext(),e.message.toString())
                        }
                    }
                    d.setNegativeButton("No"){v,m->
                        v.dismiss()
                    }
                    d.show()
                }else{
                    startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }

    private fun setupUserDetails(u: LiveData<UserEntity>){
        u.observe(viewLifecycleOwner){

            with(binding){
                if(!it.firstName.isNullOrEmpty() && !it.lastName.isNullOrEmpty()){
                    txtUserNameFrag.text = it.firstName + " " + it.lastName
                }else{
                    txtUserNameFrag.text = "-----"
                }
                txtUserEmailFrag.text = it.emailId

                if(!it.address.isNullOrEmpty()){
                    txtUserAddressFrag.text = it.address
                }else{
                    txtUserAddressFrag.text = "-"
                }
                if(it.phoneNumber.isNotEmpty()){
                    txtUserPhoneFrag.text = it.phoneNumber
                }else{
                    txtUserPhoneFrag.text = "-"
                }
                if(it.pinCode != null){
                    txtUserPincodeFrag.text = it.pinCode.toString()
                }else{
                    txtUserPincodeFrag.text = "-"
                }
            }
        }
    }

    fun logout(email : String,token : String){
        try{
            val r = NetworkService.networkInstance.logout(email,token)
            r.enqueue(object : Callback<SimpleResponse?> {
                override fun onResponse(
                    call: Call<SimpleResponse?>,
                    response: Response<SimpleResponse?>
                ) {
                    val r = response.body()!!
                    if(r.success){
                        makeToast(requireContext(),"Logged Out")
                        logout()
                        Handler().postDelayed({
                            requireActivity().finish()
                        },2000)
                    }
                }
                override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {

                }
            })
        }catch (e : Exception){

        }

    }

    private fun logout() {
        val sharedPreference = requireActivity().getSharedPreferences("user_e", Context.MODE_PRIVATE)
        val ed = sharedPreference.edit()
        ed.putString("email",null)
        ed.commit()
        CoroutineScope(Dispatchers.IO).launch{
            requireActivity().preferenceDataStoreAuth.edit {
                it[stringPreferencesKey(DataStore.JWT_TOKEN)] = ""
            }
        }
    }

    suspend fun getToken(key : String) : String? {
        val data = requireActivity().preferenceDataStoreAuth.data.first()
        return data[stringPreferencesKey(key)]
    }
}
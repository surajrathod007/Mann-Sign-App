package com.surajmanshal.mannsign

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.data.model.auth.User
import com.surajmanshal.mannsign.databinding.ActivityProfileEditBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.repository.Repository
import com.surajmanshal.mannsign.room.LocalDatabase
import com.surajmanshal.mannsign.room.user.UserDao
import com.surajmanshal.mannsign.room.user.UserEntity
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.utils.auth.LoadingScreen
import com.surajmanshal.mannsign.utils.loadRoundedImageWithUrl
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileEdit : AppCompatActivity() {


    lateinit var binding: ActivityProfileEditBinding
    lateinit var d : LoadingScreen
    lateinit var dd : Dialog
    lateinit var imageUploading : ImageUploading
    lateinit var userDatabase : UserDao
    var mUser : User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_edit)
        imageUploading = ImageUploading(this)
        d = LoadingScreen(this)
        dd = d.loadingScreen()

        val sharedPreferences = getSharedPreferences("user_e", Context.MODE_PRIVATE)
        val e = sharedPreferences.getString("email", "no email")

        userDatabase = LocalDatabase.getDatabase(this).userDao()

        val user = userDatabase.getUser(e!!)
        user.observe(this){
            if(it.firstName==null){
                // fetch from server
                val user = MutableLiveData<User>().apply {
                    observe(this@ProfileEdit){
                        with(binding){
                            editEmailName.setText(it.emailId)
                            editFirstName.setText(it.firstName)
                            editLastName.setText(it.lastName)
                            editPhone.setText(it.phoneNumber)
                            editAddress.setText(it.address)
//                            it.pinCode?.let { it1 -> etPinCode.setText(it1) }
                            Glide.with(this@ProfileEdit).load(it.profileImage?.let { it1 ->
                                Functions.urlMaker(
                                    it1
                                )
                            }).circleCrop().into(binding.ivProfilePic)
                        }
                    }
                }
                CoroutineScope(Dispatchers.IO).launch {
                        Repository().fetchUserByEmail(it.emailId).enqueue(object :
                            Callback<User?> {
                            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                                response.body()?.let { user.postValue(it) }
                            }

                            override fun onFailure(call: Call<User?>, t: Throwable) {
                                println("Failed to fetch user : $t")
                            }

                        })
                    }
                    return@observe
                }

            Glide.with(this).load(it.profileImage?.let { it1 -> Functions.urlMaker(it1) })
                .error(R.drawable.person_user).circleCrop().into(binding.ivProfilePic)
            mUser.profileImage = it.profileImage
            binding.editFirstName.setText(it.firstName)
            binding.editLastName.setText(it.lastName)
            binding.editAddress.setText(it.address)
            binding.editPhone.setText(it.phoneNumber)
            binding.editEmailName.setText(it.emailId)
            it.pinCode?.let { it1 -> binding.etPinCode.setText(it1.toString()) }
        }


        imageUploading.imageUploadResponse.observe(this){ response ->
            if(response.success){
                val data = response.message as String /*as LinkedTreeMap<String, Any>*/
                binding.ivProfilePic.loadRoundedImageWithUrl(data)
                CoroutineScope(Dispatchers.IO).launch {
                    val res = NetworkService.networkInstance.updateUserProfilePic(
                        binding.editEmailName.text.toString(),
                        data
                    )
                    mUser.profileImage = data
                    withContext(Dispatchers.Main){
                        dd.hide()
                        Toast.makeText(this@ProfileEdit, "Profile Picture Updated", Toast.LENGTH_SHORT).show()
                    }
                }
            }else Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }

        with(binding) {

            ivProfilePic.setOnClickListener { imageUploading.chooseProfileImageFromGallary() }

            btnUpdateProfile.setOnClickListener {
                onBackPressed()
            }


        }
        fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    }

    override fun onBackPressed() {
        saveProfile()
        super.onBackPressed()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode== Activity.RESULT_OK){

                if(data!=null){
                    try{
                        val selectedImageUri = data.data!!
                        imageUploading.imageUri = selectedImageUri
                        Glide.with(this).load(selectedImageUri).circleCrop().into(binding.ivProfilePic)
                        CoroutineScope(Dispatchers.IO).launch {
                            imageUploading.imageUri?.let {
                                if(requestCode == Constants.CHOOSE_PROFILE_IMAGE)
                                    imageUploading.apply {
                                        withContext(Dispatchers.Main){
                                            dd.show()
                                        }
                                        sendProfileImage(createImageMultipart())
                                    }
                            }
                        }
                    }catch(e : java.lang.Exception){
                        e.printStackTrace()
                    }
                }

        }else{
            Toast.makeText(this, "Req canceled", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun saveProfile(){
        with(binding){
            val user = UserEntity(
                emailId = editEmailName.text.toString(),
                firstName = editFirstName.text.toString(),
                lastName = editLastName.text.toString(),
                phoneNumber = editPhone.text.toString(),
                address = editAddress.text.toString(),
            ).apply {
                if(etPinCode.text.isNotEmpty()) pinCode = etPinCode.text.toString().toInt()
                mUser.profileImage?.let { profileImage = it }
            }

            try {

                GlobalScope.launch(Dispatchers.IO) {
                    val res = NetworkService.networkInstance.updateUser(
                        User(
                            emailId = editEmailName.text.toString(),
                            firstName = editFirstName.text.toString(),
                            lastName = editLastName.text.toString(),
                            address = editAddress.text.toString(),
                            token = "",
                            phoneNumber = user.phoneNumber,
                            pinCode = user.pinCode,
                            profileImage = mUser.profileImage
                        )
                    )
                    if (res.success) {
                        userDatabase.updateUser(user)
                    }
                }
                Toast.makeText(
                    this@ProfileEdit,
                    "Saved",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(this@ProfileEdit, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
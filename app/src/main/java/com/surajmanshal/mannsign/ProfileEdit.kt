package com.surajmanshal.mannsign

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.data.model.auth.User
import com.surajmanshal.mannsign.databinding.ActivityProfileEditBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.room.LocalDatabase
import com.surajmanshal.mannsign.room.user.UserDao
import com.surajmanshal.mannsign.room.user.UserEntity
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.auth.LoadingScreen
import com.surajmanshal.mannsign.utils.loadRoundedImageWithUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileEdit : SecuredScreenActivity() {


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
        mUser = intent.extras?.get("user") as User

        userDatabase = LocalDatabase.getDatabase(this).userDao()

        // Profile Image Uploading
        imageUploading.imageUploadResponse.observe(this){ response ->
            if(response.success){
                val data = response.message as String /*as LinkedTreeMap<String, Any>*/
//                binding.ivProfilePic.loadRoundedImageWithUrl(data)
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
            }
            else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                println(response.message)
            }
        }

        with(binding) {

            mUser.let {
                editFirstName.setText(it.firstName)
                editLastName.setText(it.lastName)
                editAddress.setText(it.address)
                editPhone.setText(it.phoneNumber)
                editEmailName.setText(it.emailId)
                it.pinCode?.let { it1 -> binding.etPinCode.setText(it1.toString()) }
                it.gstNo?.let { gstNo -> binding.etGstNo.setText(gstNo) }
            }

            ivProfilePic.apply {
                mUser.profileImage?.let { loadRoundedImageWithUrl(it) }
                setOnClickListener { imageUploading.chooseProfileImageFromGallary() }
            }

            btnUpdateProfile.setOnClickListener {
                onBackPressed()
            }
        }
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
                gstNo = etGstNo.text.toString()
            ).apply {
                if(etPinCode.text.isNotEmpty()) pinCode = etPinCode.text.toString().toInt()
                mUser.profileImage?.let { profileImage = it }
            }

            try {

                CoroutineScope(Dispatchers.IO).launch {
                    val res = NetworkService.networkInstance.updateUser(
                        User(
                            emailId = editEmailName.text.toString(),
                            firstName = editFirstName.text.toString(),
                            lastName = editLastName.text.toString(),
                            address = editAddress.text.toString(),
                            token = "",
                            phoneNumber = user.phoneNumber,
                            pinCode = user.pinCode,
                            profileImage = mUser.profileImage,
                            gstNo = user.gstNo
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
package com.surajmanshal.mannsign

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.internal.LinkedTreeMap
import com.surajmanshal.mannsign.data.model.auth.User
import com.surajmanshal.mannsign.databinding.ActivityProfileEditBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.room.UserDatabase
import com.surajmanshal.mannsign.room.UserEntity
import com.surajmanshal.mannsign.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileEdit : AppCompatActivity() {


    lateinit var binding: ActivityProfileEditBinding
    lateinit var imageUploading : ImageUploading
    var mUser : User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_edit)
        imageUploading = ImageUploading(this)

        val sharedPreferences = getSharedPreferences("user_e", Context.MODE_PRIVATE)
        val e = sharedPreferences.getString("email", "no email")

        val db = UserDatabase.getDatabase(this).userDao()

        val user = db.getUser(e!!)
        user.observe(this) {
            Glide.with(this).load(it.profileImage?.toUri()).into(binding.ivProfilePic)
            binding.editFirstName.setText(it.firstName)
            binding.editLastName.setText(it.lastName)
            binding.editAddress.setText(it.address)
            binding.editPhone.setText(it.phoneNumber)
            binding.editEmailName.setText(it.emailId)
            binding.etPinCode.setText(it.pinCode.toString())
        }


        imageUploading.imageUploadResponse.observe(this){ response ->
            if(response.success){
                val data = response.data as LinkedTreeMap<String, Any>
                CoroutineScope(Dispatchers.IO).launch {
                    val res = NetworkService.networkInstance.updateUserProfile(
                        binding.editEmailName.text.toString(),
                        data["url"].toString()
                    )
                    mUser.profileImage = data["url"].toString()
                }
            }else Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }

        with(binding) {

            ivProfilePic.setOnClickListener { imageUploading.chooseImageFromGallary() }

            btnUpdateProfile.setOnClickListener {
                val user = UserEntity(
                    emailId = editEmailName.text.toString(),
                    firstName = editFirstName.text.toString(),
                    lastName = editLastName.text.toString(),
                    phoneNumber = editPhone.text.toString(),
                    address = editAddress.text.toString(),
                ).apply {
                    if(etPinCode.text.isNotEmpty()) pinCode = etPinCode.text.toString().toInt()
                    imageUploading.imageUri?.let { profileImage = it.toString() }
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
                                profileImage = user.profileImage
                            )
                        )
                        if (res.success) {
                            db.updateUser(user)
                        }
                    }
                    Toast.makeText(
                        this@ProfileEdit,
                        "Profile Updated Succesfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this@ProfileEdit, e.message, Toast.LENGTH_SHORT).show()
                }

            }


        }
        fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode== Activity.RESULT_OK){
            if(requestCode == Constants.CHOOSE_IMAGE){
                if(data!=null){
                    try{
                        val selectedImageUri = data.data!!
                        imageUploading.imageUri = selectedImageUri
                        Glide.with(this).load(selectedImageUri).into(binding.ivProfilePic)
                        CoroutineScope(Dispatchers.IO).launch {
                            imageUploading.imageUri?.let {
                                imageUploading.setupImage()
                            }
                        }
                    }catch(e : java.lang.Exception){
                        e.printStackTrace()
                    }
                }
            }
        }else{
            Toast.makeText(this, "Req canceled", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
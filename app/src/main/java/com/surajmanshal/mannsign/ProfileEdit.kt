package com.surajmanshal.mannsign

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.data.model.auth.User
import com.surajmanshal.mannsign.databinding.ActivityProfileEditBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.room.LocalDatabase
import com.surajmanshal.mannsign.room.user.UserDao
import com.surajmanshal.mannsign.room.user.UserEntity
import com.surajmanshal.mannsign.ui.activity.CartActivity
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.PhotoPicker
import com.surajmanshal.mannsign.utils.applySystemBarInsets
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
    var navigatedFrom : String? = null

    private val photoPicker = PhotoPicker(this) { uri -> onProfileImagePicked(uri) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_edit)
        binding.root.applySystemBarInsets()
        onBackPressedDispatcher.addCallback(this, onBackCallback)
        imageUploading = ImageUploading(this)
        d = LoadingScreen(this)
        dd = d.loadingScreen()
        mUser = intent.extras?.get("user") as User
        navigatedFrom = intent.getStringExtra(Constants.NAV_KEY)

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
                setOnClickListener { photoPicker.launch() }
            }

            btnUpdateProfile.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private val onBackCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val isSaved = saveProfile()
            if (!isSaved) {
                return
            }
            when (navigatedFrom) {
                Constants.NAV_CART -> navigateToCart()
                Constants.NAV_AUTH -> navigateToMain()
            }
            if (navigatedFrom != null) return

            finish()
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    private fun navigateToCart() {
            startActivity(Intent(this,CartActivity::class.java))
        finish()
    }

    private fun onProfileImagePicked(uri: Uri) {
        imageUploading.imageUri = uri
        Glide.with(this).load(uri).circleCrop().into(binding.ivProfilePic)
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) { dd.show() }
            imageUploading.sendProfileImage(imageUploading.createImageMultipart())
        }
    }

    private fun saveProfile() : Boolean{
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

            val remoteUser = User(
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

            if(remoteUser.firstName.isNullOrBlank() or remoteUser.lastName.isNullOrBlank()){
                Toast.makeText(this@ProfileEdit, "Fill First name and Lastname", Toast.LENGTH_SHORT).show()
                return false
            }
            if (!remoteUser.hasValidPhoneNumber() /*&& !remoteUser.phoneNumber.isBlank()*/){
                Toast.makeText(this@ProfileEdit, "Invalid Phone no.", Toast.LENGTH_SHORT).show()
                return false
            }
            if (remoteUser.address.isNullOrBlank()){
                Toast.makeText(this@ProfileEdit, "Address is required", Toast.LENGTH_SHORT).show()
                return false
            }
            if (!remoteUser.hasValidPinCode() /*&& remoteUser.pinCode != null*/){
                Toast.makeText(this@ProfileEdit, "Invalid Pincode", Toast.LENGTH_SHORT).show()
                return false
            }
            try {

                CoroutineScope(Dispatchers.IO).launch {
                    val res = NetworkService.networkInstance.updateUser( remoteUser
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
        return true
    }
}
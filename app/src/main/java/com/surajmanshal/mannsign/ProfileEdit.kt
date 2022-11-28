package com.surajmanshal.mannsign

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.data.model.auth.User
import com.surajmanshal.mannsign.databinding.ActivityProfileEditBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.room.UserDatabase
import com.surajmanshal.mannsign.room.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileEdit : AppCompatActivity() {


    lateinit var binding: ActivityProfileEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_edit)


        val sharedPreferences = getSharedPreferences("user_e", Context.MODE_PRIVATE)
        val e = sharedPreferences.getString("email", "no email")

        val db = UserDatabase.getDatabase(this).userDao()

        val user = db.getUser(e!!)
        user.observe(this) {
            Glide.with(this).load(it.profilePic).into(binding.ivProfilePic)
            binding.editFirstName.setText(it.firstName)
            binding.editLastName.setText(it.lastName)
            binding.editAddress.setText(it.address)
            binding.editPhone.setText(it.phoneNumber)
            binding.editEmailName.setText(it.emailId)
            binding.etPinCode.setText(it.pinCode.toString())
        }

        with(binding) {
            binding.btnUpdateProfile.setOnClickListener {
                val user = UserEntity(
                    emailId = editEmailName.text.toString(),
                    firstName = editFirstName.text.toString(),
                    lastName = editLastName.text.toString(),
                    phoneNumber = editPhone.text.toString(),
                    address = editAddress.text.toString(),

                ).apply {
                    if(etPinCode.text.isNotEmpty()) pinCode = etPinCode.text.toString().toInt()
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
}
package com.surajmanshal.mannsign

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.surajmanshal.mannsign.data.model.auth.User
import com.surajmanshal.mannsign.databinding.ActivityProfileEditBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.room.UserDatabase
import com.surajrathod.authme.database.UserEntity
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
            binding.editFirstName.setText(it.firstName)
            binding.editLastName.setText(it.lastName)
            binding.editAddress.setText(it.address)
            binding.editPhone.setText(it.mobileNo)
            binding.editEmailName.setText(it.emailId)
        }

        with(binding) {
            binding.btnUpdateProfile.setOnClickListener {
                val user = UserEntity(
                    emailId = editEmailName.text.toString(),
                    firstName = editFirstName.text.toString(),
                    lastName = editLastName.text.toString(),
                    mobileNo = editPhone.text.toString(),
                    address = editAddress.text.toString(),
                    token = "",
                    otp = ""
                )

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
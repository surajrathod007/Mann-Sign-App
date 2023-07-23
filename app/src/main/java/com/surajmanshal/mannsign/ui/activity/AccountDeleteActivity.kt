package com.surajmanshal.mannsign.ui.activity

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.data.model.auth.LoginReq
import com.surajmanshal.mannsign.data.response.SimpleResponse
import com.surajmanshal.mannsign.databinding.ActivityAccountDeleteBinding
import com.surajmanshal.mannsign.network.NetworkService
import com.surajmanshal.mannsign.utils.Functions.makeToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountDeleteActivity : AppCompatActivity() {

    var email: String? = null
    lateinit var binding: ActivityAccountDeleteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = getSharedPreferences("user_e", Context.MODE_PRIVATE)
        email = sharedPreferences?.getString("email", "")
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnDeleteBack.setOnClickListener {
            finish()
        }
        binding.btnDeleteMyAccount.setOnClickListener {
            performDeletion(LoginReq(
                emailId = email!!,
                password = binding.edPasswordDelete.text.toString(),
            ))
        }
    }

    private fun showDeleteDialog() {
        val d = AlertDialog.Builder(this)
        d.setTitle("Do you want to delete account?")
        d.setMessage("After it you can not recover your account")
        d.setPositiveButton("Yes") { d, v ->
            deleteUser(email)
        }
        d.setNegativeButton("No") { d, v ->
            d.dismiss()
        }
        d.show()
    }

    private fun deleteUser(email: String?) {
        if (!email.isNullOrEmpty()) {

            val d = ProgressDialog(this)
            d.setTitle("Please wait...")
            val r = NetworkService.networkInstance.deleteUser(email)
            r.enqueue(object : Callback<SimpleResponse?> {
                override fun onResponse(
                    call: Call<SimpleResponse?>,
                    response: Response<SimpleResponse?>
                ) {
                    val re = response.body()
                    if (re != null) {
                        if (re.success) {
                            clearEverything()
                            d.dismiss()
                        } else {
                            makeToast(this@AccountDeleteActivity, re.message)
                            d.dismiss()
                        }
                    }

                }

                override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                    makeToast(this@AccountDeleteActivity, t.message.toString())
                    d.dismiss()
                }
            })
        } else {
            makeToast(this, "Email empty")
        }
    }

    private fun clearEverything() {
        makeToast(this, "Account deleted")
    }

    private fun performDeletion(loginReq: LoginReq) {

        val r = NetworkService.networkInstance.checkUserPass(loginReq)

        r.enqueue(object : Callback<SimpleResponse?> {
            override fun onResponse(
                call: Call<SimpleResponse?>,
                response: Response<SimpleResponse?>
            ) {
                val rs = response.body()!!
                if (rs.success) {
                    showDeleteDialog()
                } else {
                    makeToast(this@AccountDeleteActivity, rs.message)
                }

            }

            override fun onFailure(call: Call<SimpleResponse?>, t: Throwable) {
                makeToast(this@AccountDeleteActivity, t.message.toString())

            }
        })

    }


}
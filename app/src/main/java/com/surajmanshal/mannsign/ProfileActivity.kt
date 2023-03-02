package com.surajmanshal.mannsign

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.data.model.auth.User
import com.surajmanshal.mannsign.databinding.ActivityProfileBinding
import com.surajmanshal.mannsign.repository.Repository
import com.surajmanshal.mannsign.room.LocalDatabase
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.utils.auth.DataStore.preferenceDataStoreAuth
import com.surajmanshal.mannsign.utils.loadRoundedImageWithUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileActivity : AppCompatActivity() {

    lateinit var btnEdit : Button
    lateinit var btnLogout : Button

    lateinit var binding: ActivityProfileBinding
    lateinit var txtUserName : TextView
    lateinit var txtEmail : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        txtUserName = findViewById(R.id.tvDisplayName)
        txtEmail = findViewById(R.id.tvUserName)
        btnLogout = findViewById(R.id.btnLogout)

        setupCards()




        btnEdit = findViewById(R.id.btnEditProfile)
        btnEdit.setOnClickListener {
            val intent = Intent(this@ProfileActivity, ProfileEdit::class.java)
            //intent.putExtra("user",user)
            startActivity(intent)
        }



        btnLogout.setOnClickListener {

            try{

            GlobalScope.launch(Dispatchers.IO){
                preferenceDataStoreAuth.edit {
                    it.clear()
                }
            }

                Toast.makeText(this.applicationContext,"Logged Out !",Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, AuthenticationActivity::class.java))
                finish()
            }catch (e : Exception){
                Toast.makeText(this.applicationContext,e.message,Toast.LENGTH_SHORT).show()
            }
        }



    }

    override fun onResume() {
        val db = LocalDatabase.getDatabase(this).userDao()
        val sharedPreferences = getSharedPreferences("user_e", Context.MODE_PRIVATE)
        val e = sharedPreferences.getString("email","no email")
        val user = db.getUser(e!!)

        user.observe(this) { it ->
            txtEmail.text = it.emailId
            if(it.firstName==null){
                // Fetch the user details
                val user = MutableLiveData<User>().apply {
                    observe(this@ProfileActivity){
                        txtUserName.text = it.firstName+" "+it.lastName
                        Glide.with(this@ProfileActivity).load(it.profileImage?.let { it1 ->
                            Functions.urlMaker(
                                it1
                            )
                        }).circleCrop().into(binding.layoutProfile.ivProfilePic)
                    }
                }
                CoroutineScope(Dispatchers.IO).launch {
                    Repository().fetchUserByEmail(it.emailId).enqueue(object :
                        Callback<User?>{
                        override fun onResponse(call: Call<User?>, response: Response<User?>) {
                            response.body()?.let { user.postValue(it) }
                        }

                        override fun onFailure(call: Call<User?>, t: Throwable) {
                            println("Failed to fetch user : $t")
                        }

                    })
                }
            }else{
                with(it){
                    txtUserName.text = firstName
                    profileImage?.let { it1 ->
                        binding.layoutProfile.ivProfilePic.loadRoundedImageWithUrl(
                            it1
                        )
                    }
                }
            }
        }
        super.onResume()
    }

    fun setupCards() {
        with(binding) {
            // Title
            cvOrders.tvUserName.text = "Orders"
            cvNotifications.tvUserName.text = "Notifications"
            cvShippings.tvUserName.text = "Shipping"
            // Navigators
            cvOrders.root.setOnClickListener {
            // TODO : Handled while Integration
                Toast.makeText(this@ProfileActivity, "Opens Orders", Toast.LENGTH_SHORT).show()
            }
            cvNotifications.root.setOnClickListener {
            // TODO : Handled while Integration
                Toast.makeText(this@ProfileActivity, "Opens Notification", Toast.LENGTH_SHORT).show()
            }
            cvShippings.root.setOnClickListener {
            // TODO : Handled while Integration
                Toast.makeText(this@ProfileActivity, "Opens Shipping", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
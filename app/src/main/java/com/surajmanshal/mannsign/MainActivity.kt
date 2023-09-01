package com.surajmanshal.mannsign


import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.phonepe.intent.sdk.api.PhonePe
import com.surajmanshal.mannsign.databinding.ActivityMainBinding
import com.surajmanshal.mannsign.utils.auth.DataStore

class MainActivity : SecuredScreenActivity() {

    interface MainActivityBackPressListener {
        fun onActivityBackPressed()
    }

    lateinit var binding: ActivityMainBinding
    var isRead = false
    var isWrite = false
    var token: String? = ""

    val listners = mutableListOf<MainActivityBackPressListener>()

    var backPressedTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PhonePe.init(this)
        println("pkg sign "+PhonePe.getPackageSignature())
        window.statusBarColor = Color.BLACK
        token = intent.getStringExtra(DataStore.JWT_TOKEN)


        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                Toast.makeText(this, "Notifications $it", Toast.LENGTH_SHORT).show()
            }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    Log.e(ContentValues.TAG, "User accepted the notifications!")
//                    sendNotification(this)
                    Toast.makeText(this, "Notifications Permitted", Toast.LENGTH_SHORT).show()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Snackbar.make(
                        binding.root,
                        "The user denied the notifications ):",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Settings") {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            /*val uri: Uri =
                                Uri.fromParts("com.onesilisondiode.geeksforgeeks", packageName, null)
                            intent.data = uri*/
                            startActivity(intent)
                        }
                        .show()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
        /*permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            isRead = it[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isRead
            isWrite = it[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWrite
        }*/


//        requestPermission()
        //setupViewPager()

    }

    override fun onBackPressed() {
        listners.forEach {
            it.onActivityBackPressed()
        }
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(this, "Press back again to leave the app.", Toast.LENGTH_LONG).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    fun registerListener(listener: MainActivityBackPressListener) {
        listners.add(listener)
    }

    fun removeListener(listener: MainActivityBackPressListener) {
        listners.remove(listener)
    }

    private fun setupViewPager() {
        //val flist = listOf(HomeFragment(token),CustomOrderFragment(),UserProfileFragment(token))
        //binding.viewPager.adapter = MainViewPagerAdapter(flist,this@MainActivity)
        //binding.bottomNavigationView.setupWithViewPager2(binding.viewPager)
    }

    /*fun requestPermission(){

        //check permission already granted or not
        isRead = ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        isWrite = ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        var permissionRequest : MutableList<String> = ArrayList()

        if(!isRead){
            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if(!isWrite){
            permissionRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if(permissionRequest.isNotEmpty()){
            //request permission
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }


    }*/
}
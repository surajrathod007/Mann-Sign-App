package com.surajmanshal.mannsign


import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.surajmanshal.mannsign.adapter.MainViewPagerAdapter
import com.surajmanshal.mannsign.databinding.ActivityMainBinding
import com.surajmanshal.mannsign.ui.fragments.CustomOrderFragment
import com.surajmanshal.mannsign.ui.fragments.HomeFragment
import com.surajmanshal.mannsign.ui.fragments.UserProfileFragment
import com.surajmanshal.mannsign.utils.auth.DataStore

class MainActivity : AppCompatActivity() {


    lateinit var binding : ActivityMainBinding
    var isRead = false
    var isWrite = false
    var token : String? = ""

    lateinit var permissionLauncher : ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.BLACK
        token = intent.getStringExtra(DataStore.JWT_TOKEN)



        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            isRead = it[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isRead
            isWrite = it[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWrite
        }



        requestPermission()
        //setupViewPager()

    }

    private fun setupViewPager(){
        //val flist = listOf(HomeFragment(token),CustomOrderFragment(),UserProfileFragment(token))
        //binding.viewPager.adapter = MainViewPagerAdapter(flist,this@MainActivity)
        //binding.bottomNavigationView.setupWithViewPager2(binding.viewPager)
    }

    fun requestPermission(){

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


    }
}
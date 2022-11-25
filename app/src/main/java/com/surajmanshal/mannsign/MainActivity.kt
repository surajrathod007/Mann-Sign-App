package com.surajmanshal.mannsign


import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.surajmanshal.mannsign.adapter.MainViewPagerAdapter
import com.surajmanshal.mannsign.databinding.ActivityMainBinding
import com.surajmanshal.mannsign.ui.activity.CartActivity
import com.surajmanshal.mannsign.ui.fragments.CustomOrderFragment
import com.surajmanshal.mannsign.ui.fragments.HomeFragment
import com.surajmanshal.mannsign.ui.fragments.UserProfileFragment
import com.surajmanshal.mannsign.viewmodel.CartViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nl.joery.animatedbottombar.AnimatedBottomBar

class MainActivity : AppCompatActivity() {


    lateinit var binding : ActivityMainBinding
    var isRead = false
    var isWrite = false

    lateinit var permissionLauncher : ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            isRead = it[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isRead
            isWrite = it[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWrite
        }

        requestPermission()
        setupViewPager()

    }

    private fun setupViewPager(){
        val flist = listOf(HomeFragment(),CustomOrderFragment(),UserProfileFragment())
        binding.viewPager.adapter = MainViewPagerAdapter(flist,this@MainActivity)
        binding.bottomNavigationView.setupWithViewPager2(binding.viewPager)
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
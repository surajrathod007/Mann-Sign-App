package com.surajmanshal.mannsign

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupViewPager()

    }

    private fun setupViewPager(){
        val flist = listOf(HomeFragment(),CustomOrderFragment(),UserProfileFragment())
        binding.viewPager.adapter = MainViewPagerAdapter(flist,this@MainActivity)
        binding.bottomNavigationView.setupWithViewPager2(binding.viewPager)
    }
}
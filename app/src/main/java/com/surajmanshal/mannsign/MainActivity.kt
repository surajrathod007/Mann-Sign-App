package com.surajmanshal.mannsign


import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import com.surajmanshal.mannsign.databinding.ActivityMainBinding
import com.surajmanshal.mannsign.utils.auth.DataStore

class MainActivity : SecuredScreenActivity() {


    lateinit var binding : ActivityMainBinding
    var isRead = false
    var isWrite = false
    var token : String? = ""

//    lateinit var permissionLauncher : ActivityResultLauncher<Array<String>>

    var backPressedTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.BLACK
        token = intent.getStringExtra(DataStore.JWT_TOKEN)



        /*permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            isRead = it[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: isRead
            isWrite = it[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWrite
        }*/



//        requestPermission()
        //setupViewPager()

    }

    override fun onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(this, "Press back again to leave the app.", Toast.LENGTH_LONG).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    private fun setupViewPager(){
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
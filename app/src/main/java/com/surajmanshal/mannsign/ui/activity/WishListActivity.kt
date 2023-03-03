package com.surajmanshal.mannsign.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.surajmanshal.mannsign.databinding.ActivityWishListBinding
import com.surajmanshal.mannsign.room.wishlist.WishListDao
import com.surajmanshal.mannsign.utils.hide

class WishListActivity : AppCompatActivity() {
    lateinit var wishListDao : WishListDao
    val binding by lazy { ActivityWishListBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wishListDao.getWishList().observe(this){
            binding.apply {
                setContentView(root)
                rvWishlist.apply {
                    layoutManager = GridLayoutManager(this@WishListActivity,2)
                }
                toolbar.apply {
                    tvToolbarTitle.text = "My Wishlist"
                    ivBackButton.setOnClickListener {
                        onBackPressed()
                    }
                    ivOptions.hide()
                }
            }
        }
    }
}
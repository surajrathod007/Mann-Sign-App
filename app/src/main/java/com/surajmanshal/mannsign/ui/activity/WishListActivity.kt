package com.surajmanshal.mannsign.ui.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.surajmanshal.mannsign.SecuredScreenActivity
import com.surajmanshal.mannsign.adapter.recyclerview.WishlistAdapter
import com.surajmanshal.mannsign.databinding.ActivityWishListBinding
import com.surajmanshal.mannsign.room.LocalDatabase
import com.surajmanshal.mannsign.room.wishlist.WishListDao
import com.surajmanshal.mannsign.utils.hide
import com.surajmanshal.mannsign.viewmodel.WishListViewModel

class WishListActivity : SecuredScreenActivity() {

    lateinit var vm : WishListViewModel
    lateinit var wishListDao : WishListDao
    val binding by lazy { ActivityWishListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProvider(this)[WishListViewModel::class.java]
        wishListDao = LocalDatabase.getDatabase(this).wishListDao()

        // Observers ------------------------------------------------------------------
        wishListDao.getWishList().observe(this){ it ->
            vm.getMyWishList(it.map { it.productId })
        }

        binding.rvWishlist.apply {
            layoutManager = GridLayoutManager(this@WishListActivity,2)
            vm.wishListedProducts.observe(this@WishListActivity){
                adapter =
                    WishlistAdapter(this@WishListActivity,
                        it,
                        this@WishListActivity,
                        wishListDao
                    )
            }
        }
        // Views Initialization -----------------------------------------------------------
        binding.apply {
            setContentView(root)
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
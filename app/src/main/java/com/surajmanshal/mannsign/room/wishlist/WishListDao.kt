package com.surajmanshal.mannsign.room.wishlist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query


@Dao
interface WishListDao {

    @Insert(onConflict = REPLACE)
    fun addToWishlist(item : WishListItem)

    @Query("delete from wishListTable where productId = :productId")
    fun removeFromWishlist(productId : Int)

    @Query("Select * from wishListTable")
    fun getWishList() : LiveData<List<WishListItem>>

    @Query("Select Count(productId) from wishListTable where productId = :productId")
    fun exist(productId : Int) : LiveData<Int>

}
package com.surajmanshal.mannsign.room.wishlist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishListTable")
data class WishListItem(
    @PrimaryKey
    val productId : Int,
    val timeStamp : Long
)

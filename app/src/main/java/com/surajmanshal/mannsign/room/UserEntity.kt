package com.surajmanshal.mannsign.room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "userTable")
data class UserEntity(


    @PrimaryKey
    val emailId : String,
    val firstName : String="",
    val lastName : String="",
    val mobileNo : String ="",
    val address : String? = null,
    val token : String? = null,
    val otp : String? = null
)

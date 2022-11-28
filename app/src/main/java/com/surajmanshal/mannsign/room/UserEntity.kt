package com.surajmanshal.mannsign.room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "userTable")
data class UserEntity(


    @PrimaryKey
    var emailId : String ="",
    var password : String ="",
    val firstName : String? = null,
    val lastName : String? = null,
    var phoneNumber : String = "",
    val profileImage : String? = null,
    val address : String? = null,
    var pinCode : Int? = null,
    var profilePic : String? = null
)

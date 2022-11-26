package com.surajmanshal.mannsign.data.model.auth

data class User(
    var emailId : String ="",
    var password : String ="",
    val firstName : String? = null,
    val lastName : String? = null,
    var phoneNumber : String = "",
    val profileImage : String? = null,
    val address : String? = null,
    val pinCode : Int? = null,
    var token : String = "",
    var deviceId : String? = null
)

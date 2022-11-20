package com.surajmanshal.mannsign.data.model.auth


import java.security.Principal

//import java.security.Principal

data class User(
    val emailId : String ="",
    val password : String ="",
    val firstName : String = "",
    val lastName : String = "",
    val phoneNumber : String = "",
    val profileImage : String = "",
    val address : String = "",
    val pinCode : Int = 0,
    var token : String = "",
    var deviceId : String = ""
)

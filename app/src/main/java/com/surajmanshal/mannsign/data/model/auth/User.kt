package com.surajmanshal.mannsign.data.model.auth

data class User(
    var emailId : String ="",
    var password : String ="",
    var firstName : String? = null,
    val lastName : String? = null,
    var phoneNumber : String = "",
    var profileImage : String? = null,
    val address : String? = null,
    var pinCode : Int? = null,
    var token : String = "",
    var deviceId : String? = null,
    var profilePic : String? = null
) : java.io.Serializable

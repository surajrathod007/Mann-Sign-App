package com.surajmanshal.mannsign.data.model.auth

data class LoginReq(
    val emailId : String,
    val password : String,
    val deviceID : String = ""
)
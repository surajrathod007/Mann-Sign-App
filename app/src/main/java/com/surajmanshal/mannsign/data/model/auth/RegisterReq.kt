package com.surajmanshal.mannsign.data.model.auth

data class RegisterReq(
    val firstName : String,
    val lastName : String,
    val emailId : String,
    val password : String
)

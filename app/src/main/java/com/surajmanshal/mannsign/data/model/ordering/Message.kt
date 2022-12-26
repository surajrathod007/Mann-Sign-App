package com.surajmanshal.mannsign.data.model.ordering

import com.surajmanshal.mannsign.data.model.auth.User

data class Message(
    val message : String,
    val sender : User,
    val createdAt : Long
)

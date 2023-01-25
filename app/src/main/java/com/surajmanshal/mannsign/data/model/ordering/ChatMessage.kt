package com.surajmanshal.mannsign.data.model.ordering

data class ChatMessage(
    val orderId : String,
    val emailId : String,
    val message : String,
    val timeStamp : String,
    var imageUrl : String? = null
)

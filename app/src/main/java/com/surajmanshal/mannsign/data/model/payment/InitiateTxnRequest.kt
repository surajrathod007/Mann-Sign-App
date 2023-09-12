package com.surajmanshal.mannsign.data.model.payment

data class InitiateTxnRequest(
    val appPkg : String,
    val orderId : String ,
    val amount : Int,
    val mobileNo : String
)
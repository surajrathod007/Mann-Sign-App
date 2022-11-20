package com.surajmanshal.mannsign.data.model.ordering

import java.time.LocalDate

data class Transaction(
    val transactionId : String,
    val emailId : String,
    val mode : Int,
    val deliveryCharge : Float,
    val date : LocalDate,
    val amount : Float,
    val orderId : String
)

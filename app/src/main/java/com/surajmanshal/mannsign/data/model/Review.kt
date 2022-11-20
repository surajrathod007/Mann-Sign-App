package com.surajmanshal.mannsign.data.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Review (
    val reviewId : Int?=null,
    val productId : Int,
    val rating : Int,
    val comment : String,
    val emailId : String,
    val reviewDate : LocalDateTime
)
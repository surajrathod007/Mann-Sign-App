package com.surajmanshal.mannsign.data.model.ordering

import java.io.Serializable

data class Order(
    val orderId : String,
    val emailId : String,
    val orderDate : String,
//    val orderItems : List<OrderItem>? = null,
    val quantity : Int = 0,
    var trackingUrl : String?=null,
    var days : Int? = null,
    var orderStatus : Int = 0,
    var paymentStatus : Int = 0,
    var total : Float,
    val discount : Float = 0.0f,
    val totalRecieved : Float,
    val deliveryCharge : Float,
    val invoiceNo : String?
) : Serializable

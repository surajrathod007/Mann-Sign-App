package com.surajmanshal.mannsign.data.model

data class GST (
    val itemPrice: Float,
    val cGST: Float,
    val sGST: Float
){
    companion object {
        fun getInstance(labelAmount: Float): GST {
            val itemPrice = labelAmount / 1.18F
            val internalTax = itemPrice * 0.09F
            val externalTax = itemPrice * 0.09F
            return GST(itemPrice, internalTax, externalTax)
        }
    }
}


package com.surajmanshal.mannsign.data.model.ordering

import com.surajmanshal.mannsign.data.model.Variant
import com.surajmanshal.mannsign.data.model.product.Product
import java.io.Serializable

data class OrderItem(
    var product: Product? = null,
    var variant : Variant? = null,
    var quantity : Int,
    var totalPrice : Float
) : Serializable

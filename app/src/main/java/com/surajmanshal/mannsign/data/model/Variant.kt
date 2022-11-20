package com.surajmanshal.mannsign.data.model

import java.io.Serializable

data class Variant(
    val variantId : Int? = null,
    val productId : Int,
    val sizeId : Int,
    val materialId : Int,
    val languageId : Int,
    val variantPrice : Float
) : Serializable

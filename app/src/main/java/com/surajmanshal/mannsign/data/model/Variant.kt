package com.surajmanshal.mannsign.data.model

import java.io.Serializable

data class Variant(
    val variantId : Int? = null,
    var productId : Int? = null,
    var sizeId : Int? = null,
    var materialId : Int? = null,
    var languageId : Int? = null,
    var variantPrice : Float? = null
) : Serializable

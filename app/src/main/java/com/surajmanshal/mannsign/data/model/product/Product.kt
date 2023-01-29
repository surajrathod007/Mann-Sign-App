package com.surajmanshal.mannsign.data.model.product

import com.surajmanshal.mannsign.data.model.Image
import com.surajmanshal.mannsign.data.model.Size
import java.io.Serializable

data class Product(
    var productId: Int,
    var images: List<Image>? = null,
    var sizes: List<Size>? = null,
    var materials: List<Int>? = null,
    var languages: List<Int>? = null,
    var basePrice: Float? = null,
    var typeId: Int? = null,
    var posterDetails: Poster? = null,
    var category: Int? = null,
    var subCategory: Int? = null,
    var boardDetails: ACPBoard? = null,
    var bannerDetails: Banner? = null,
    var productCode : String? = null
    ) : Serializable

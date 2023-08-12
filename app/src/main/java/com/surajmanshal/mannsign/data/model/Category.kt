package com.surajmanshal.mannsign.data.model

data class Category(
    val id : Int? = null,
    var name : String="",
    val subCategories : List<SubCategory>? = null,
    val imgUrl : String? = null
)

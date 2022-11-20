package com.surajmanshal.mannsign.data.model

data class Category(
    val id : Int? = null,
    val name : String,
    val subCategories : List<SubCategory>? = null
)

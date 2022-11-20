package com.surajmanshal.mannsign.data.model

import java.io.Serializable

data class Image(
    val id : Int,
    val url : String,
    val description : String? = null
) : Serializable

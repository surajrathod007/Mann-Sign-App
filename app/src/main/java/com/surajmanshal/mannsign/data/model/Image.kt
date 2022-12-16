package com.surajmanshal.mannsign.data.model

import java.io.Serializable

data class Image(
    val id : Int=0,
    val url : String,
    val description : String? = null,
    val languageId : Int
) : Serializable

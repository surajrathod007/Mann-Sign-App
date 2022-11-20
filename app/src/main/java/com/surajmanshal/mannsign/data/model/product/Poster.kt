package com.surajmanshal.mannsign.data.model.product

import java.io.Serializable

data class Poster(
    val title : String,
    val short_desc : String,
    val long_desc : String?
) : Serializable

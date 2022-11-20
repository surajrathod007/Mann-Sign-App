package com.surajmanshal.mannsign.data.model.product

import java.io.Serializable

data class ACPBoard(
    val text : String,
    val emboss : Float,
    val rgb : String, // CMYK could be generated at the time of storing it in table
    val font : Int
) : Serializable

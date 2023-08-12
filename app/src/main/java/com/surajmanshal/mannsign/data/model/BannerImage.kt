package com.surajmanshal.mannsign.data.model

import alirezat775.lib.carouselview.CarouselModel
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes

class BannerImage constructor(private var imgUrl: String) : CarouselModel() {
    fun getId(): String {
        return imgUrl
    }
}
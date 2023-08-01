package com.surajmanshal.mannsign.data.model

import alirezat775.lib.carouselview.CarouselModel
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes

class BannerImage constructor(@DrawableRes private var imgUrl: Int) : CarouselModel() {
    fun getId(): Int {
        return imgUrl
    }
}
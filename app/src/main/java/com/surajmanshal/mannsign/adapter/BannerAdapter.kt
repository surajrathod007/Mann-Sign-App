package com.surajmanshal.mannsign.adapter

import alirezat775.lib.carouselview.CarouselAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.data.model.BannerImage

class BannerAdapter : CarouselAdapter() {

    private val EMPTY_ITEM = 0
    private val NORMAL_ITEM = 1

    private var vh: CarouselAdapter.CarouselViewHolder? = null
    var onClick: OnClick? = null

    fun setOnClickListener(onClick: OnClick?) {
        this.onClick = onClick
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val v = inflater.inflate(R.layout.banner_item_layout, parent, false)
        vh = MyViewHolder(v)
        return vh as MyViewHolder
    }


    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        when (holder) {
            is MyViewHolder -> {
                vh = holder
                val model = getItems()[position] as BannerImage
                Glide.with(holder.itemView.context).load(model.getId()).into((vh as MyViewHolder).imgPoster)
            }
        }
    }

    inner class MyViewHolder(itemView: View) : CarouselViewHolder(itemView) {

        var imgPoster: ImageView = itemView.findViewById(R.id.imgBanner)

        init {
            imgPoster.setOnClickListener { onClick?.click(getItems()[adapterPosition] as BannerImage) }
        }

    }
    interface OnClick {
        fun click(model: BannerImage)
    }
}
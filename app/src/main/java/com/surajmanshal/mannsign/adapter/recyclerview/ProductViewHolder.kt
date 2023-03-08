package com.surajmanshal.mannsign.adapter.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsign.databinding.ProductItemLayoutBinding

class ProductViewHolder(val binding: ProductItemLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val imgProduct = binding.imgProductImage
    val txtProductName = binding.txtProductName
    val txtProductPrice = binding.txtProductPrice
    val txtProductCategory = binding.txtProductCategory
    val btnAddToWishList = binding.btnAddToWishlist
    val productCard = binding.productCard
}
package com.surajmanshal.mannsign.adapter.recyclerview

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.databinding.ProductItemLayoutBinding
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.viewmodel.HomeViewModel

class ProductAdapter(val context: Context, val list: List<Product>, val vm: HomeViewModel) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ProductItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imgProduct = binding.imgProductImage
        val txtProductName = binding.txtProductName
        val txtProductPrice = binding.txtProductPrice
        val txtProductCategory = binding.txtProductCategory
        val btnAddToWishList = binding.btnAddToWishlist
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            ProductItemLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val data = list[position]
        //todo : fetch banners
        if (data.posterDetails != null) {
            with(holder) {
                txtProductName.text = data.posterDetails!!.title
                txtProductCategory.text = data.subCategory.toString()
                txtProductPrice.text = "$ " + data.basePrice.toString()
                btnAddToWishList.setOnClickListener {
                    Functions.makeToast(it.context, "Add to wishlist")
                }
                Glide.with(context).load(Uri.parse(Functions.urlMaker(data.images?.get(0)?.url.toString())))
                    .into(imgProduct)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
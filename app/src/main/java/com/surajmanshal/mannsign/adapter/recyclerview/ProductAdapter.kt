package com.surajmanshal.mannsign.adapter.recyclerview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.data.model.product.Product
import com.surajmanshal.mannsign.databinding.ProductItemLayoutBinding
import com.surajmanshal.mannsign.ui.activity.ProductDetailsActivity
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.Functions
import com.surajmanshal.mannsign.viewmodel.HomeViewModel

class ProductAdapter(val context: Context, val list: List<Product>, val vm: HomeViewModel= HomeViewModel()) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ProductItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imgProduct = binding.imgProductImage
        val txtProductName = binding.txtProductName
        val txtProductPrice = binding.txtProductPrice
        val txtProductCategory = binding.txtProductCategory
        val btnAddToWishList = binding.btnAddToWishlist
        val productCard = binding.productCard
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
        val context = holder.itemView.context
        //todo : fetch banners
        if (data.posterDetails != null) {
            with(holder) {
                Glide.with(context).load(Uri.parse(Functions.urlMaker(data.images?.get(0)?.url.toString())))
                    .into(imgProduct)
                txtProductName.text = data.posterDetails!!.title
                txtProductCategory.text = data.subCategory.toString()
                txtProductPrice.text = "$ " + data.basePrice.toString()
                btnAddToWishList.setOnClickListener {
                    Functions.makeToast(it.context, "Add to wishlist")
                }
                productCard.setOnClickListener {
                    context.startActivity(Intent(context,ProductDetailsActivity::class.java).apply {
                        putExtra(Constants.PRODUCT,data)
                    })
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
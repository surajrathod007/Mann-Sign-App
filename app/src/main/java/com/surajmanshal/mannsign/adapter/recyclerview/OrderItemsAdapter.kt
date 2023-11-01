package com.surajmanshal.mannsign.adapter.recyclerview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.data.model.ordering.OrderItem
import com.surajmanshal.mannsign.databinding.OrderProductItemsLayoutBinding
import com.surajmanshal.mannsign.ui.activity.ProductDetailsActivity
import com.surajmanshal.mannsign.utils.Constants
import com.surajmanshal.mannsign.utils.Functions

class OrderItemsAdapter(val c: Context, val list: List<OrderItem>) :
    RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder>() {

    /*init {
        println("Items:" + list.toString())
    }*/
    class OrderItemViewHolder(val binding: OrderProductItemsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val txtOrderItemTitle = binding.txtOrderItemTitle

        //        val txtOrderItemBasePrice = binding.txtOrderItemBasePrice
        val txtOrderItemQty = binding.txtOrderItemQty
        val txtOrderItemTotalPrice = binding.txtOrderItemTotalPrice
        val imgProduct = binding.imgProductOrderItem
        val txtProductType = binding.txtProductType

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        return OrderItemViewHolder(
            OrderProductItemsLayoutBinding.inflate(
                LayoutInflater.from(c),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        /*val o = list[position]

        with(holder) {
            txtOrderItemQty.text = "Quantity : " + o.quantity.toString()
//            txtOrderItemBasePrice.text = "Base Price : ₹" + o.product!!.basePrice.toString()
            txtOrderItemTotalPrice.text = "₹" + o.totalPrice.toString()
            with(o) {
//                val url = product?.images?.let {
//                    if(it.isNotEmpty()){
//                        Functions.urlMaker(it[0].url)
//                    }
//                }
                if (!product?.images.isNullOrEmpty()) {
                    Glide.with(c)
                        .load(Uri.parse(Functions.urlMaker(product?.images?.get(0)?.url.toString())))
                        .placeholder(
                            R.drawable.no_photo
                        )
                        .into(imgProduct)
                }else{
                    Functions.makeToast(c, "No image url !")
                }
                //Glide.with(imgProduct.context).load(url).into(imgProduct)
            }
            if (o.product!!.posterDetails != null) {
                txtOrderItemTitle.text = o.product!!.posterDetails!!.title.toString()
                txtProductType.text = "Poster"
            }
            if (o.product!!.boardDetails != null) {
                txtProductType.text = "ACP Board"
            }
            if (o.product!!.bannerDetails != null) {
                txtProductType.text = "Banner"
            }
            itemView.setOnClickListener {
                //Functions.makeToast(c, "Open Product Details !")
                c.startActivity(Intent(c, ProductDetailsActivity::class.java).apply {
                    putExtra(Constants.PRODUCT, o.product)
                })
            }
        }*/
        val order = list[position]

        with(holder) {
            txtOrderItemQty.text = "Quantity : ${order.quantity}"
            txtOrderItemTotalPrice.text = "₹${order.totalPrice}"

            order.product?.let { pro ->
                pro.images?.let {
                    if (it.isNotEmpty()) {
                        val imageUrl = Functions.urlMaker(it.first().url)
                        Glide.with(c)
                            .load(Uri.parse(imageUrl))
                            .placeholder(R.drawable.no_photo)
                            .into(imgProduct)
                    } else {
                        Functions.makeToast(c, "Failed to load image!")
                    }
                }
                when {
                    pro.posterDetails != null -> {
                        txtOrderItemTitle.text = pro.posterDetails!!.title
                        txtProductType.text = "Poster"
                    }

                    pro.boardDetails != null -> {
                        txtProductType.text = "ACP Board"
                    }

                    pro.bannerDetails != null -> {
                        txtProductType.text = "Banner"
                    }
                }
                itemView.setOnClickListener {
                    val intent = Intent(c, ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.PRODUCT, pro)
                    c.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
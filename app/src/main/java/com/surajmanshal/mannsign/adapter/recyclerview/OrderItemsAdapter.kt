package com.surajmanshal.mannsign.adapter.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.data.model.ordering.OrderItem
import com.surajmanshal.mannsign.databinding.OrderProductItemsLayoutBinding
import com.surajmanshal.mannsign.utils.Functions

class OrderItemsAdapter(val c: Context, val list: List<OrderItem>) :
    RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder>() {

    class OrderItemViewHolder(val binding: OrderProductItemsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val txtOrderItemTitle = binding.txtOrderItemTitle
        val txtOrderItemBasePrice = binding.txtOrderItemBasePrice
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
        val o = list[position]

        with(holder) {
            txtOrderItemQty.text = "Quantity : " + o.quantity.toString()
            txtOrderItemBasePrice.text = "Base Price : ₹" + o.product!!.basePrice.toString()
            txtOrderItemTotalPrice.text = "₹" + o.totalPrice.toString()
            with(o) {
                val url = product?.images?.get(0)?.let {
                    Functions.urlMaker(it.url)
                }
                Glide.with(imgProduct.context).load(url).into(imgProduct)
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
                Functions.makeToast(c, "Open Product Details !")
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
package com.surajmanshal.mannsign.adapter.recyclerview

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsign.data.model.ordering.Order
import com.surajmanshal.mannsign.databinding.OrderItemLayoutBinding
import com.surajmanshal.mannsign.ui.activity.OrderDetailsActivity
import com.surajmanshal.mannsign.utils.Constants

class OrdersAdapter(val context: Context, val list: List<Order>) :
    RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(val binding: OrderItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val txtOrderId = binding.txtOrderId
        val txtOrderDate = binding.txtOrderDate
        val txtTotalProduct = binding.txtOrderProductTotal
        val txtOrderStatus = binding.txtOrderStatus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(
            OrderItemLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val d = list[position]
        with(holder) {
            txtOrderId.text = "Order Id : " + d.orderId.toString()
            txtOrderDate.text = "Order Date : " + d.orderDate.toString()
            txtTotalProduct.text = "Total Products : " + d.orderItems?.size.toString()
            when (d.orderStatus) {
                Constants.ORDER_PENDING -> {
                    holder.txtOrderStatus.text = "Pending"
                }
                Constants.ORDER_CONFIRMED -> {
                    holder.txtOrderStatus.text = "Confirmed"
                }
                Constants.ORDER_PROCCESSING -> {
                    holder.txtOrderStatus.text = "Processing"
                }
                Constants.ORDER_READY -> {
                    holder.txtOrderStatus.text = "Ready"
                }
                Constants.ORDER_DELIVERED -> {
                    holder.txtOrderStatus.text = "Delivered"
                }
                Constants.ORDER_CANCELED -> {
                    holder.txtOrderStatus.text = "Canceled"
                }
            }
            itemView.setOnClickListener {
                val i = Intent(context, OrderDetailsActivity::class.java)
                i.putExtra("id", d.orderId)
                context.startActivity(i)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
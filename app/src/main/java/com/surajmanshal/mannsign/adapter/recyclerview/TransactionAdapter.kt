package com.surajmanshal.mannsign.adapter.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.data.model.ordering.TransactionItem
import com.surajmanshal.mannsign.databinding.TransactionItemLayoutBinding

class TransactionAdapter(val lst: List<TransactionItem>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val binding: TransactionItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val transactionId = binding.txtTransactionId
        val email = binding.txtTransactionEmail
        val amount = binding.txtTransactionAmount
        val date = binding.txtTransactionDate
        val charge = binding.txtTransactionDeliveryCharge
        val mode = binding.txtTransactionMode
        val orderId = binding.txtTransactionOrderId
        val btnExpand = binding.btnExpand
        val transactionDetailView = binding.transactionDetailsView
    }


    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val l = lst[position].transaction
        val visible = lst[position].visible

        with(holder) {
            transactionId.text = l.transactionId
            email.text = l.emailId
            amount.text = "₹" + l.amount.toString()
            date.text = l.date.toString()
            charge.text = "₹" + l.deliveryCharge.toString()
            when (l.mode) {
                0 -> {
                    mode.text = "Paytm"
                }
                1 -> {
                    mode.text = "Cash On Delivery"
                }
            }
            orderId.text = l.orderId
            transactionDetailView.visibility = if(visible) View.VISIBLE else View.GONE
            btnExpand.setImageResource(if(visible) R.drawable.ic_baseline_keyboard_arrow_up_24 else R.drawable.ic_baseline_keyboard_arrow_down_24)
        }

        holder.btnExpand.setOnClickListener {
            lst[position].visible = !lst[position].visible
            notifyItemChanged(position)
        }


    }

    override fun getItemCount(): Int {
        return lst.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(
            TransactionItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


}
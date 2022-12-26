package com.surajmanshal.mannsign.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.surajmanshal.mannsign.data.model.ordering.Message
import com.surajmanshal.mannsign.databinding.ItemMeMessageBinding
import com.surajmanshal.mannsign.databinding.ItemOtherMessageBinding


class ChatAdapter(val context: Context, val msg: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_SEND_TYPE = 1
        const val VIEW_RECEIVE_TYPE = 2
    }

    override fun getItemCount(): Int {
        return msg.size
    }

    override fun getItemViewType(position: Int): Int {
        val msg = msg[position]
        if (msg.sender.emailId == "surajsinhrathod75@gmail.com") {
            return VIEW_SEND_TYPE
        } else {
            return VIEW_RECEIVE_TYPE
        }
    }

    private class SendMessageViewHolder(val binding: ItemMeMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val txtMessageMe = binding.txtMessageMe
        val txtMessageTimeMe = binding.txtMessageTimeMe
        fun bind(msg: Message) {
            txtMessageMe.text = msg.message
            txtMessageTimeMe.text = msg.createdAt.toString()
        }
    }

    private class ReceiveMessageViewHolder(val binding: ItemOtherMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val txtMessageOther = binding.txtMessageOther
        val txtMessageTimeOther = binding.txtMessageTimeOther

        fun bind(msg: Message) {
            txtMessageOther.text = msg.message
            txtMessageTimeOther.text = msg.createdAt.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_SEND_TYPE) {
            return SendMessageViewHolder(
                ItemMeMessageBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )
        } else {
            return ReceiveMessageViewHolder(
                ItemOtherMessageBinding.inflate(LayoutInflater.from(context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val m = msg[position]
        when(holder.itemViewType){
            VIEW_SEND_TYPE -> {
                (holder as SendMessageViewHolder).bind(m)
            }
            VIEW_RECEIVE_TYPE -> {
                (holder as ReceiveMessageViewHolder).bind(m)
            }
        }
    }
}
package com.surajmanshal.mannsign.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.surajmanshal.mannsign.R
import com.surajmanshal.mannsign.data.model.ordering.ChatMessage
import com.surajmanshal.mannsign.data.model.ordering.Message
import com.surajmanshal.mannsign.databinding.ItemChatImageViewReceiveBinding
import com.surajmanshal.mannsign.databinding.ItemChatImageViewSentBinding
import com.surajmanshal.mannsign.databinding.ItemMeMessageBinding
import com.surajmanshal.mannsign.databinding.ItemOtherMessageBinding
import com.surajmanshal.mannsign.utils.Functions


class ChatAdapter(val context: Context, val msg: List<ChatMessage>, val email: String?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_SEND_TYPE = 1
        const val VIEW_RECEIVE_TYPE = 2
        const val VIEW_IMAGE_SENT = 3
        const val VIEW_IMAGE_RECEIVE = 4
    }

    override fun getItemCount(): Int {
        return msg.size
    }

    override fun getItemViewType(position: Int): Int {
        val msg = msg[position]
        if (!email.isNullOrEmpty()) {
            return if (msg.emailId == email) {
                if (!msg.imageUrl.isNullOrEmpty()) {
                    VIEW_IMAGE_SENT
                } else {
                    VIEW_SEND_TYPE
                }
            } else {
                if (!msg.imageUrl.isNullOrEmpty()) {
                    VIEW_IMAGE_RECEIVE
                } else {
                    VIEW_RECEIVE_TYPE
                }
            }
        } else {
            return VIEW_SEND_TYPE
        }
    }

    private class SendMessageViewHolder(val binding: ItemMeMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val txtMessageMe = binding.txtMessageMe
        val txtMessageTimeMe = binding.txtMessageTimeMe
        fun bind(msg: ChatMessage) {
            txtMessageMe.text = msg.message
            txtMessageTimeMe.text = Functions.timeStampToDate(msg.timeStamp)
        }
    }

    private class ReceiveMessageViewHolder(val binding: ItemOtherMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val txtMessageOther = binding.txtMessageOther
        val txtMessageTimeOther = binding.txtMessageTimeOther

        fun bind(msg: ChatMessage) {
            txtMessageOther.text = msg.message
            txtMessageTimeOther.text = Functions.timeStampToDate(msg.timeStamp)
        }
    }

    private class SentImageViewHolder(val binding: ItemChatImageViewSentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val imgSent = binding.imgChatSent
        val txtImageText = binding.txtChatImageSent
        val txtTimeStamp = binding.txtChatImageTimeSent

        fun bind(msg: ChatMessage, c: Context) {
            if (!msg.message.isEmpty()) {
                txtImageText.text = msg.message
            }
            if (!msg.imageUrl.isNullOrEmpty()) {
                Glide.with(c)
                    .load(Uri.parse(Functions.urlMakerChat(msg.imageUrl!!)))
                    .placeholder(
                        R.drawable.no_photo
                    )
                    .into(imgSent)
            }
            txtTimeStamp.text = Functions.timeStampToDate(msg.timeStamp)
        }
    }

    private class ReceiveImageViewHolder(val binding: ItemChatImageViewReceiveBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val imgReceive = binding.imgChatReceive
        val txtImageText = binding.txtChatImageReceive
        val txtTimeStamp = binding.txtChatImageTimeReceive

        fun bind(msg: ChatMessage, c: Context) {
            if (!msg.message.isEmpty()) {
                txtImageText.text = msg.message
            }
            if (!msg.imageUrl.isNullOrEmpty()) {
                Glide.with(c)
                    .load(Uri.parse(Functions.urlMakerChat(msg.imageUrl!!)))
                    .placeholder(
                        R.drawable.no_photo
                    )
                    .into(imgReceive)
                //Functions.makeToast(c, "${msg.imageUrl}")
            }
            txtTimeStamp.text = Functions.timeStampToDate(msg.timeStamp)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            VIEW_SEND_TYPE -> {
                return SendMessageViewHolder(
                    ItemMeMessageBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                    )
                )
            }
            VIEW_RECEIVE_TYPE -> {
                return ReceiveMessageViewHolder(
                    ItemOtherMessageBinding.inflate(LayoutInflater.from(context), parent, false)
                )
            }
            VIEW_IMAGE_SENT -> {
                return SentImageViewHolder(
                    ItemChatImageViewSentBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                    )
                )
            }
            VIEW_IMAGE_RECEIVE -> {
                return ReceiveImageViewHolder(
                    ItemChatImageViewReceiveBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                return ReceiveMessageViewHolder(
                    ItemOtherMessageBinding.inflate(LayoutInflater.from(context), parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val m = msg[position]
        when (holder.itemViewType) {
            VIEW_SEND_TYPE -> {
                (holder as SendMessageViewHolder).bind(m)
            }
            VIEW_RECEIVE_TYPE -> {
                (holder as ReceiveMessageViewHolder).bind(m)
            }
            VIEW_IMAGE_SENT -> {
                (holder as SentImageViewHolder).bind(m, context)
            }
            VIEW_IMAGE_RECEIVE -> {
                (holder as ReceiveImageViewHolder).bind(m, context)
            }
        }
    }
}